FROM python:3.9-slim
WORKDIR /usr/src/app

COPY ./shipping/requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY ./shipping/shipping-job.py ./

ARG DB_URL
ARG DB_PASSWORD

ENV DB_URL=${DB_URL}
ENV DB_PASSWORD=${DB_PASSWORD}


CMD ["python", "shipping-job.py"]
