# 🚀 Docker Quick Start - 3 นาทีเริ่มต้น

## ⚡ เริ่มต้นด่วน

```bash
# 1. รัน Docker Compose
docker-compose up -d --build

# 2. ตรวจสอบสถานะ
docker-compose ps

# 3. ดู logs
docker-compose logs -f app
```

## 🔗 เข้าถึง

- **API**: http://localhost:8088
- **MySQL**: localhost:3306

## 📊 ข้อมูล Login

- **Username**: admin001, housekeeper001-003, hirer001-003
- **Password**: password123

## 🛑 หยุดการทำงาน

```bash
# หยุด
docker-compose down

# หยุดและลบข้อมูล
docker-compose down -v
```

## 📝 คำสั่งที่ใช้บ่อย

```bash
# Restart
docker-compose restart

# Rebuild
docker-compose up -d --build

# ดู logs
docker-compose logs -f

# เข้า container
docker exec -it maebanjumpen-app sh
docker exec -it maebanjumpen-db mysql -u root -p1234 maebanjumpen
```

## 🔧 Troubleshooting

### Port ถูกใช้งาน
```bash
# หยุด MySQL บน host
brew services stop mysql

# หรือเปลี่ยน port ใน docker-compose.yml
```

### Database ไม่พร้อม
```bash
# รอ 10-30 วินาที แล้วตรวจสอบ
docker-compose logs db
```

### Rebuild Image
```bash
docker-compose build --no-cache
docker-compose up -d
```

---

📖 **คู่มือเต็ม**: อ่าน [DOCKER_README.md](DOCKER_README.md)

