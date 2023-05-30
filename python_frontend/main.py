import psycopg2

from os import environ as env, name
from dotenv import load_dotenv

load_dotenv()

centers = []

#establishing the connection
conn = psycopg2.connect(
   database="postgres", user=env['DB_USERNAME'], password=env['DB_PASSWORD'], host='35.222.180.129'
)
#Creating a cursor object using the cursor() method
cursor = conn.cursor()



#Populate dropdown
cursor.execute("SELECT DISTINCT center FROM uscis_stats")
data = cursor.fetchall()
print(data)

for t in data:
    centers.append(t[0])

for c in centers:
    cursor.execute(f"SELECT currently_handling, read_at FROM uscis_stats WHERE center = '{c}'")
    data = cursor.fetchall()
    print(c)
    print(data)



#Closing the connection
conn.close()
