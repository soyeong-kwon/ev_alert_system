<?php
header("Content-Type: text/html; charset=UTF-8");
$conn= new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com", "dajung", "12345678", "DajungDB");
mysqli_query($conn,'SET NAMES utf8');

$id= $_POST['id'];
$pw= $_POST['pw'];

$sql= "select * from login where id='$id' and pw='$pw'";

$res= $conn->query($sql);

if(mysqli_fetch_array($res)!=NULL){
    echo "<script>location.href='tables.html';</script>";
}
else{
    echo "<script>location.href='login.html';</script>";
}

?>