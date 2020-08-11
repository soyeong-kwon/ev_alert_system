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
$res3= $conn->query($sql3);

$response = array();
$response["success"]=false;

// DB에 IMEI가 있을 때, 위치정보 DB에 저장
if(mysqli_fetch_array($res1)!=NULL){
    $res2= $conn->query($sql2);
    $response["success"]=true;
}

// 위치정보 앱에 뿌려주기
while($row=mysqli_fetch_array($res3)){
    $response["Latitude"]= $row["Latitude"];
    $response["Longitude"]= $row["Longitude"];
}

echo json_encode($response);
?>