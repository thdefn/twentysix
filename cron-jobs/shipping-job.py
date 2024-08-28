import os
import pymysql

connection = None

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
