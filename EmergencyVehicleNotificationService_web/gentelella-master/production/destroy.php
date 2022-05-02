<?php
header("Content-Type: text/html; charset=UTF-8");
$conn = new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com", "dajung", "12345678", "DajungDB");
mysqli_query($conn, 'SET NAMES utf8');

$phonenum = $_POST['PhoneNum'];

$response = array();

$sql_sel = "select * from address where phonenum='$phonenum'";
$sql_del = "delete from address where phonenum='$phonenum'";

$res = $conn->query($sql_sel);
if(mysqli_fetch_array($res)!=NULL){
    $conn->query($sql_del);
}

$response["success"] = true;
echo json_encode($response);
?>