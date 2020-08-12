<?php
header("Content-Type: text/html; charset=UTF-8");
$conn = new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com","dajung","12345678","DajungDB");
mysqli_query($conn,'SET NAMES utf8');

$latitude = $_POST['Latitude'];
$longitude = $_POST['Longitude'];
$phonenum = $_POST['phonenum'];

$sql1= "select * from personinfo where phonenum = '$phonenum'";
$sql2= "insert into address(Latitude, Longitude) values('$latitude','$longitude')";
$sql3= "select * from address";
$sql4= "delete from address";


$response = array();
$response["success"]=false;


$res1= $conn->query($sql1);
// DB에 IMEI가 있을 때, 위치정보 DB에 저장
if(mysqli_fetch_array($res1)!=NULL){
    $res2= $conn->query($sql2);
    $response["success"]=true;
}


$num=0;

$res3= $conn->query($sql3);
// 위치정보 앱에 뿌려주기
while($row=mysqli_fetch_array($res3)){
    $response["number"]=$num;
    $numstr = (string)$num;
    $response["Latitude$numstr"]= $row["Latitude"];
    $response["Longitude$numstr"]= $row["Longitude"];
    $num++;
}

echo json_encode($response);
?>