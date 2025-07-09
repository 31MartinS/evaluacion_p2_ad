from locust import HttpUser, task, between
import random
from datetime import datetime, timedelta
import string

def random_device_id():
    return "sensor-" + ''.join(random.choices(string.ascii_lowercase + string.digits, k=6))

def random_timestamp():
    return (datetime.utcnow() - timedelta(minutes=random.randint(0, 1440))).isoformat() + "Z"

class DistributedHealthSystemUser(HttpUser):
    host = "http://localhost:8000"
    wait_time = between(1, 3)

    @task(4)
    def post_vital_sign(self):
        tipo = random.choice(["heart-rate", "oxygen-saturation", "blood-pressure-systolic", "blood-pressure-diastolic", "BatteryLow"])
        valor = self.get_value_for_type(tipo)

        data = {
            "deviceId": random_device_id(),
            "type": tipo,
            "value": valor,
            "timestamp": random_timestamp()
        }

        self.client.post("/conjunta/2p/vital-signs", json=data)

    @task(2)
    def get_vitals_by_device(self):
        device_id = random.choice(["sensor-apc", "sensor-xyz", "sensor-abc"])
        self.client.get(f"/conjunta/2p/vital-signs/{device_id}")

    @task(2)
    def get_all_alerts(self):
        self.client.get("/conjunta/2p/health/alerts")

    @task(2)
    def get_all_vitals(self):
        self.client.get("/conjunta/2p/health/vitals")

    @task(1)
    def health_status_check(self):
        self.client.get("/conjunta/2p/health")

    @task(1)
    def notifier_status_check(self):
        self.client.get("/conjunta/2p/notifier")

    @task(1)
    def simulate_email(self):
        body = {"to": "test@example.com", "message": "Simulación desde Locust"}
        self.client.post("/conjunta/2p/notifier/mock/email", json=body)

    @task(1)
    def simulate_sms(self):
        body = {"to": "+50212345678", "message": "SMS simulado desde Locust"}
        self.client.post("/conjunta/2p/notifier/mock/sms", json=body)

    def get_value_for_type(self, tipo):
        return {
            "heart-rate": random.uniform(45, 180),
            "oxygen-saturation": random.uniform(85, 99),
            "blood-pressure-systolic": random.uniform(100, 180),
            "blood-pressure-diastolic": random.uniform(60, 120),
            "BatteryLow": 0.0
        }.get(tipo, 1.0)
