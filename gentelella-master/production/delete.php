<?php
header("Content-Type: text/html; charset=UTF-8");
$conn=new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com","dajung","12345678","DajungDB");
mysqli_query($conn,'SET NAMES utf8');

$name=$_POST['name'];
$phonenum=$_POST['phonenum'];

$personinfo_check="select * from personinfo where name='$name' and phonenum='$phonenum'";
$delete_check="delete from personinfo where name='$name' and phonenum='$phonenum'";
$sql2="alter table personinfo auto_increment=1";
$sql3="set @count=0";
$sql4="update personinfo set id=@count:=@count+1";

$res_1=$conn->query($personinfo_check);

if(mysqli_fetch_array($res_1)!=NULL)
{
    $res_del1=$conn->query($delete_check);  
    $res=$conn->query($sql2);
    $res=$conn->query($sql3);
    $res=$conn->query($sql4);
   
    echo "<script>location.href = 'delete.html';</script>";
}
else{
    echo "<script>alert('등록된 정보가 없습니다.');</script>";
    echo "<script>location.href = 'delete.html';</script>";
}



?>