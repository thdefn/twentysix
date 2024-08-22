import random
from locust import HttpUser, between, task

class WebsiteUser(HttpUser):
    wait_time = between(1, 2)  # 각 작업 사이의 대기 시간 (1~2초)
    user_count = 0

    def on_start(self):
        WebsiteUser.user_count += 1
        self.user_id = WebsiteUser.user_count  # 1부터 시작하는 유저 ID 할당
        self.order_id = None  # 초기화

    @task
    def create_order(self):
        user_id = str(self.user_id)  # HttpUser의 user_id 속성 사용

        headers = {
            "X-USER-ID": user_id  # 헤더에 유저 ID 포함
        }

        order_response = self.client.post("http://localhost:8084/orders",
                                          json={
                                              "products": [
                                                  {
                                                      "id": "66c437d4552c0f20d08e947f",
                                                      "quantity": 1
                                                  }
                                              ],
                                              "shouldSaveNewAddress": False,
                                              "shouldDeleteCartItem": False,
                                              "receiver": {
                                                  "isDefault": True,
                                                  "name": "송송이",
                                                  "address": "서울특별시 성북구 보문로 34로",
                                                  "zipCode": "11234",
                                                  "phone": "010-2223-1111"
                                              }
                                          },
                                          headers=headers)

        if order_response.status_code == 200:
            self.order_id = order_response.json().get("orderId")
            print(f"Order created with ID: {self.order_id}")
        else:
            print("Failed to create order")

    @task
    def checkout_and_pay(self):
        if self.order_id:
            user_id = str(self.user_id)  # HttpUser의 user_id 속성 사용

            headers = {
                "X-USER-ID": user_id  # 헤더에 유저 ID 포함
            }

            # 임의로 실패를 발생시킬 확률 (20%)
            if random.random() < 0.2:
                print("Skipping checkout and pay due to random chance")
                return

            checkout_response = self.client.get(f"http://localhost:8085/checkout/{self.order_id}",
                                                headers=headers)

            if checkout_response.status_code == 200:
                payment_response = self.client.post(f"http://localhost:8085/payments",
                                                    json={
                                                        "orderId": self.order_id,
                                                        "amount": "206815",
                                                        "paymentKey": self.order_id
                                                    }, headers=headers)
                if payment_response.status_code == 200:
                    print(f"Payment successful for order ID: {self.order_id}")
                else:
                    print(f"Payment failed for order ID: {self.order_id}")
            else:
                print(f"Checkout failed for order ID: {self.order_id}")
        else:
            print("Order ID not available for checkout and payment")

