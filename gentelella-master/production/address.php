<?php
header("Content-Type: text/html; charset=UTF-8");
$conn = new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com","dajung","12345678","DajungDB");
mysqli_query($conn,'SET NAMES utf8');

$latitude = $_POST['Latitude'];
$longitude = $_POST['Longitude'];

$sql= "insert into address(Latitude, Longitude) values('$latitude','$longitude')";
$res= $conn->query($sql);

$response = array();
$response["success"]=true;

echo json_encode($response);

?>