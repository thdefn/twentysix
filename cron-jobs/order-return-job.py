import json
import os
import pymysql
from kafka import KafkaProducer

def get_kafka_producer():
    return KafkaProducer(
        bootstrap_servers=os.getenv('KAFKA_BROKERS'),
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )

def main():
    connection = None
    producer = None

    try:
        db_url = os.getenv('DB_URL')
        db_password = os.getenv('DB_PASSWORD')

        if not db_url:
            raise ValueError("DB_URL environment variable is not set or is empty.")


        db_url = db_url.replace("jdbc:mysql://", "")
        host_port, database = db_url.split("/", 1)
        host, port = host_port.split(":")

        connection = pymysql.connect(
            host=host,
            port=int(port),
            user="root",
            password=db_password,
            database=database)

        with connection.cursor() as cursor:
            cursor.execute(
                "SELECT orderId, id, products FROM orders WHERE status = 'BEING_RETURNED'")

            result = cursor.fetchall()

            for row in result:
                order_id = row[0]
                id = row[1]
                products = row[2]

                json_dict = json.load(products)
                product_quantity_map = {
                    product_id: product_info['quantity']
                    for product_id, product_info in json_dict.items()
                }

                order_cancelled_event = {
                    'orderId': order_id,
                    'productQuantity': product_quantity_map
                }

                producer.send('order-cancelled-events', value=order_cancelled_event)
                cursor.execute(
                    "UPDATE orders SET status = %s WHERE id = %s",
                    ('RETURN_COMPLETED', id)
                )
            producer.flush()
            connection.commit()



    except Exception as e:
        print(f"An error occurred: {e}")
        if connection:
            connection.rollback()
        exit(1)

    finally:
        if connection:
            connection.close()
        if producer:
            producer.close()

    exit(0)

if __name__ == "__main__":
    main()