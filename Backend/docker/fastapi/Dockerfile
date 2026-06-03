FROM python:3.13

WORKDIR /app

# libmariadb-dev: cabeceras C necesarias para compilar el conector mariadb de Python
RUN apt-get update && apt-get install -y libmariadb-dev && rm -rf /var/lib/apt/lists/*

COPY ./requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir --upgrade -r /app/requirements.txt
RUN pip install --no-cache-dir --upgrade uvicorn

COPY ./app /app/app

# Railway inyecta $PORT automaticamente; en local se usa 8000 como fallback
CMD uvicorn app.main:app --host 0.0.0.0 --port ${PORT:-8000}
