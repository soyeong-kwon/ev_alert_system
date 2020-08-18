<?php
header("Content-Type: text/html; charset=UTF-8");
$conn=new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com","dajung","12345678","DajungDB");
mysqli_query($conn,'SET NAMES utf8');

$phonenum=$_POST['phonenum'];

$sql="delete from personinfo where phonenum='$phonenum'";
$sql1="delete from address where phonenum='$phonenum'";

$res=$conn->query($sql);
$res=$conn->query($sql1);

echo "<script>location.href='tables.html'; </script>"
?>