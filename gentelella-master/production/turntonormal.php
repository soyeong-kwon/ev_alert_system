<?php
header("Content-Type: text/html; charset=UTF-8");
$conn = new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com","dajung","12345678","DajungDB");
mysqli_query($conn,'SET NAMES utf8');

$phonenum = $_POST['PhoneNum'];

$response = array();
$response["inaddress"]=false;

$sql= "select * from address where phonenum = '$phonenum'";
$res= $conn->query($sql); // 운전자의 위치 정보

$sql1= "select * from personinfo where phonenum = '$phonenum'";
$res1= $conn->query($sql1); // 운전자 등록 여부 확인 

if(mysqli_fetch_array($res1)!=NULL){ // 등록시 
    $delete_sql="delete from address where phonenum='$phonenum'";
    $del_res=$conn->query($delete_sql); // 운전자의 위치정보 모두 삭제

    $response["inaddress"]=true;
}
$num=0;

$address_sql= "select * from address";
$address_res= $conn->query($address_sql); #모든 긴급자동차 운전자의 위치 정보 

while($row=mysqli_fetch_array($address_res)){ 
    $response["num_inaddress"]=$num;
    $numstr = (string)$num;
    
    // 긴급자동차 운전자 위치정보 배열 생성
    $response["Latitude$numstr"]= $row["Latitude"];
    $response["Longitude$numstr"]= $row["Longitude"];
    $num++;
}

echo json_encode($response);
?>