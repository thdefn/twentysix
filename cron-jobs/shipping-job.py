import os
import pymysql

connection = None

try:
    db_url = os.getenv('DB_URL')
    db_password = os.getenv('DB_PASSWORD')

    connection = pymysql.connect(
        host=db_url,
        user="root",
        password=db_password,
        database='26cm-order')

    with connection.cursor() as cursor:
        cursor.execute(
            "update orders set status = 'IN_TRANSIT' where status = 'ORDER_PLACED' and updated_at <= NOW() - INTERVAL 1 DAY")
        connection.commit()

        cursor.execute(
            "update orders set status = 'DELIVERED' where status = 'IN_TRANSIT' and updated_at <= NOW() - INTERVAL 1 DAY")
        connection.commit()

        cursor.execute(
            "update orders set status = 'ORDER_COMPLETED' where status = 'DELIVERED' and updated_at <= NOW() - INTERVAL 2 DAY")
        connection.commit()

except Exception as e:
    print(f"An error occurred: {e}")
    exit(1)

finally:
    if connection:
        connection.close()

exit(0)
