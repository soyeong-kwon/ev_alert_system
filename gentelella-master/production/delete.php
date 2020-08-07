<?php
header("Content-Type: text/html; charset=UTF-8");
$conn=new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com","dajung","12345678","DajungDB");
mysqli_query($conn,'SET NAMES utf8');

$id=$_POST['id'];
$sql="delete from personinfo where id=$id";
$res=$conn->query($sql);

echo "<script>location.href='tables.html'; </script>"
?>