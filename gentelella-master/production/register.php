<?php
$conn = new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com","dajung","12345678","DajungDB");
mysqli_query($conn,'SET NAMES utf8');

$name = $_POST['name'];
$phonenum = $_POST['phonenum'];
$cartype = $_POST['cartype'];
$IMEI = $_POST['IMEI'];
$sql= "insert into personinfo(name, phonenum, cartype, IMEI) values('$name','$phonenum','$cartype','$IMEI')";
$res= $conn->query($sql);

echo "<script> location.href='form_validation.html'; </script>"
?>