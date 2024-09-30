Khởi động lại Nginx để áp dụng cấu hình mới:
Lệnh: docker-compose exec nginx nginx -s reload

Automated renewal SSL with crontab:
Lệnh 1: crontab -e
Lệnh 2: 0 5 1 _/2 _ /usr/bin/docker compose -f /var/docker/docker-compose.yml up certbot

0: Phút 0
5: Giờ thứ 5 (5 giờ sáng)
1: Ngày 1 của tháng
_/2: Cứ mỗi 2 tháng
_: Bất kỳ ngày nào trong tuần
