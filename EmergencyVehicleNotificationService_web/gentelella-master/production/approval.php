<?php
header("Content-Type: text/html; charset=UTF-8");
$conn=new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com","dajung","12345678","DajungDB");
mysqli_query($conn,'SET NAMES utf8');

$phonenum=$_POST['phonenum'];

$sql_apv="select * from approval where phonenum='$phonenum'";
$res = $conn->query($sql_apv);
$row = mysqli_fetch_array($res);

$name = $row["name"];
$p_num = $row["phonenum"];
$email = $row["emailID"];
$cartype = $row["cartype"];

$sql_ins= "insert into personinfo(name, phonenum, emailID, cartype) values('$name','$p_num', '$email', '$cartype')";
$res=$conn->query($sql_ins);

$sql="delete from approval where phonenum='$phonenum'";
$sql2="alter table approval auto_increment=1";
$sql3="set @count=0";
$sql4="update approval set id=@count:=@count+1";

$res=$conn->query($sql);
$res=$conn->query($sql2);
$res=$conn->query($sql3);
$res=$conn->query($sql4);

echo "<script>location.href='tables.html'; </script>"
?>

