FROM python:3.9-slim
WORKDIR /usr/src/app

COPY ./order-return/requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY ./order-return/order-return-job.py ./


ARG DB_URL
ARG DB_PASSWORD
ARG KAFKA_BROKERS

ENV DB_URL=${DB_URL}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV KAFKA_BROKERS=${KAFKA_BROKERS}


CMD ["python", "order-return-job.py"]
