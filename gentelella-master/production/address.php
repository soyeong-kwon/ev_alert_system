<?php
header("Content-Type: text/html; charset=UTF-8");
$conn = new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com","dajung","12345678","DajungDB");
mysqli_query($conn,'SET NAMES utf8');

$latitude = $_POST['Latitude'];
$longitude = $_POST['Longitude'];
$IMEI = $_POST['IMEI'];

$sql1= "select * from personinfo where IMEI = '$IMEI'";
$sql2= "insert into address(Latitude, Longitude) values('$latitude','$longitude')";
$sql3= "select * from address";

$res1= $conn->query($sql1);
$res2= $conn->query($sql3);

$response = array();
$response["success"]=false;

if(mysqli_fetch_array($res1)!=NULL){
    $res2= $conn->query($sql2);
    $response["success"]=true;
}

while($row=mysqli_fetch_array($res2)){
    $response["Latitude"]= $row['Latitude'];
    $response["Longitude"]= $row['Longitude'];
}

echo json_encode($response);
?>