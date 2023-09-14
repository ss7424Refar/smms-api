## smms

### 部署
- mysql8 
```agsl
lower_case_table_names = 1
```
```agsl
sudo mysqld --initialize-insecure --user=mysql --datadir=/var/lib/mysql
```
```
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';

use mysql;
update user set host='%' where user='root';
``` 
