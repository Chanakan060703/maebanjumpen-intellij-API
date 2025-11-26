# 🐳 Docker Setup Guide - Maebanjumpen API

คู่มือการใช้งาน Docker สำหรับโปรเจค Maebanjumpen

## 📋 สิ่งที่ต้องมี

- Docker Desktop (สำหรับ Mac/Windows) หรือ Docker Engine (สำหรับ Linux)
- Docker Compose v2.0+

## 🚀 วิธีการรัน

### 1. รัน Docker Compose (แนะนำ)

```bash
# รัน services ทั้งหมด (MySQL + Spring Boot App)
docker-compose up -d

# ดู logs
docker-compose logs -f

# ดู logs เฉพาะ app
docker-compose logs -f app

# ดู logs เฉพาะ database
docker-compose logs -f db
```

### 2. หยุดการทำงาน

```bash
# หยุด services
docker-compose down

# หยุดและลบ volumes (ลบข้อมูลทั้งหมด)
docker-compose down -v
```

### 3. Rebuild Image

```bash
# Rebuild เมื่อมีการเปลี่ยนแปลง code
docker-compose up -d --build

# หรือ rebuild เฉพาะ app
docker-compose build app
docker-compose up -d app
```

## 📦 Services

### 🗄️ MySQL Database (db)
- **Image**: mysql:8.0
- **Container Name**: maebanjumpen-db
- **Port**: 3306 (host) → 3306 (container)
- **Database**: maebanjumpen
- **Username**: root
- **Password**: 1234
- **Volume**: mysql-data (persistent storage)
- **Auto-init**: รัน `database/data.sql` อัตโนมัติตอน start

### 🌐 Spring Boot Application (app)
- **Container Name**: maebanjumpen-app
- **Port**: 8088 (host) → 8088 (container)
- **Profile**: docker
- **Depends on**: db (รอให้ MySQL พร้อมก่อน)
- **Volumes**:
  - `./uploads` → `/app/uploads` (สำหรับไฟล์ที่อัปโหลด)
  - `./qr_codes` → `/app/qr_codes` (สำหรับ QR codes)

## 🔗 การเข้าถึง

### Application
- **URL**: http://localhost:8088
- **Health Check**: http://localhost:8088/actuator/health (ถ้ามี)

### MySQL Database
```bash
# เชื่อมต่อจาก host machine
mysql -h localhost -P 3306 -u root -p1234

# เชื่อมต่อจากภายใน container
docker exec -it maebanjumpen-db mysql -u root -p1234 maebanjumpen
```

## 📊 ข้อมูลเริ่มต้น

เมื่อรัน Docker Compose ครั้งแรก ระบบจะ:
1. สร้าง MySQL container
2. สร้างฐานข้อมูล `maebanjumpen`
3. รันไฟล์ `database/data.sql` อัตโนมัติ (insert ข้อมูลตัวอย่าง)
4. รัน Spring Boot application
5. Hibernate จะสร้าง/อัปเดตตารางตาม Entity classes

### ข้อมูลตัวอย่างที่ถูก Insert:
- ✅ SkillType: 4 รายการ
- ✅ SkillLevelTier: 4 ระดับ
- ✅ Login: 7 accounts
- ✅ Person: 7 คน
- ✅ PartyRole: Admin, Housekeeper, Hirer
- ✅ HousekeeperSkill: 6 skills
- ✅ Hire: 5 งาน
- ✅ Review: 3 รีวิว
- ✅ Transaction: 5 รายการ
- ✅ Penalty: 1 รายการ
- ✅ Report: 1 รายการ

### ข้อมูลการ Login:
- **Username**: admin001, housekeeper001-003, hirer001-003
- **Password**: password123 (ทั้งหมด)

## 🛠️ คำสั่งที่มีประโยชน์

### ดูสถานะ Containers
```bash
docker-compose ps
```

### เข้าไปใน Container
```bash
# เข้า app container
docker exec -it maebanjumpen-app sh

# เข้า database container
docker exec -it maebanjumpen-db bash
```

### ดู Logs แบบ Real-time
```bash
# ทุก services
docker-compose logs -f

# เฉพาะ app
docker-compose logs -f app

# เฉพาะ db
docker-compose logs -f db
```

### Restart Services
```bash
# Restart ทั้งหมด
docker-compose restart

# Restart เฉพาะ app
docker-compose restart app

# Restart เฉพาะ db
docker-compose restart db
```

### ลบข้อมูลและเริ่มใหม่
```bash
# หยุดและลบทุกอย่าง (รวม volumes)
docker-compose down -v

# รันใหม่
docker-compose up -d
```

## 🔧 Troubleshooting

### ปัญหา: Port ถูกใช้งานอยู่แล้ว

```bash
# ตรวจสอบว่า port 3306 หรือ 8088 ถูกใช้งานหรือไม่
lsof -i :3306
lsof -i :8088

# หยุด MySQL ที่รันอยู่บน host
brew services stop mysql
# หรือ
sudo systemctl stop mysql
```

### ปัญหา: Database ไม่พร้อม

```bash
# ตรวจสอบ health check
docker-compose ps

# ดู logs ของ database
docker-compose logs db

# รอให้ database พร้อม (ประมาณ 10-30 วินาที)
```

### ปัญหา: Application ไม่เชื่อมต่อ Database

```bash
# ตรวจสอบว่า db container ทำงานหรือไม่
docker-compose ps db

# ตรวจสอบ network
docker network ls
docker network inspect maebanjumpen-intellij-api_maebanjumpen-network

# Restart app
docker-compose restart app
```

### ปัญหา: ต้องการ Rebuild Image

```bash
# Rebuild ทั้งหมด
docker-compose build --no-cache

# Rebuild เฉพาะ app
docker-compose build --no-cache app

# รันใหม่
docker-compose up -d
```

## 📁 โครงสร้างไฟล์

```
.
├── Dockerfile              # สำหรับ build Spring Boot app
├── docker-compose.yml      # กำหนด services ทั้งหมด
├── .dockerignore          # ไฟล์ที่ไม่ต้อง copy เข้า Docker image
├── database/
│   └── data.sql           # SQL script สำหรับ init data
├── src/                   # Source code
├── uploads/               # Volume สำหรับไฟล์ที่อัปโหลด
└── qr_codes/              # Volume สำหรับ QR codes
```

## 🌍 Environment Variables

### Database (db service)
- `MYSQL_ROOT_PASSWORD`: 1234
- `MYSQL_DATABASE`: maebanjumpen
- `MYSQL_USER`: root
- `MYSQL_PASSWORD`: 1234

### Application (app service)
- `SPRING_PROFILES_ACTIVE`: docker
- `SPRING_DATASOURCE_URL`: jdbc:mysql://db:3306/maebanjumpen?characterEncoding=UTF-8&serverTimezone=Asia/Bangkok&useLegacyDatetimeCode=false
- `SPRING_DATASOURCE_USERNAME`: root
- `SPRING_DATASOURCE_PASSWORD`: 1234
- `SPRING_JPA_HIBERNATE_DDL_AUTO`: update
- `SPRING_SQL_INIT_MODE`: always
- `SPRING_JPA_DEFER_DATASOURCE_INITIALIZATION`: true

## 🎯 Best Practices

1. **Development**: ใช้ `docker-compose up` (ไม่ใส่ `-d`) เพื่อดู logs แบบ real-time
2. **Production**: ใช้ `docker-compose up -d` เพื่อรันใน background
3. **Backup Database**: 
   ```bash
   docker exec maebanjumpen-db mysqldump -u root -p1234 maebanjumpen > backup.sql
   ```
4. **Restore Database**:
   ```bash
   docker exec -i maebanjumpen-db mysql -u root -p1234 maebanjumpen < backup.sql
   ```

## 📝 หมายเหตุ

- ข้อมูลใน MySQL จะถูกเก็บใน Docker volume `mysql-data` จะไม่หายแม้ restart container
- ถ้าต้องการลบข้อมูลทั้งหมด ใช้ `docker-compose down -v`
- ไฟล์ที่อัปโหลดจะถูกเก็บใน `./uploads` และ `./qr_codes` บน host machine
- Application จะรอให้ MySQL พร้อมก่อนเริ่มทำงาน (ใช้ healthcheck)

