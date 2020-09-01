<?php
header("Content-Type: text/html; charset=UTF-8");
$conn=new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com","dajung","12345678","DajungDB");
mysqli_query($conn,'SET NAMES utf8');

$name=$_POST['name'];
$phonenum=$_POST['phonenum'];

$personinfo_name="select from personinfo where name='$name'";
$personinfo_phone="select from personinfo where phonenum='$phonenum'";
$delete_personinfo="delete from personinfo where name='$name";
$delete_address="delete from address where phonenum='$phonenum";

$res_1=$conn->query($personinfo_name);
$res_2=$conn->query($personinfo_phone);

if($res_1==$res_2)
{
    $res_del1=$conn->query($delete_address);
    $res_del1=$conn->query($delete_personinfo;
        
}

echo "<script>location.href='tables.html'; </script>"
?>