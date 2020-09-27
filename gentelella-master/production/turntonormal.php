<?php
header("Content-Type: text/html; charset=UTF-8");
$conn = new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com","dajung","12345678","DajungDB");
mysqli_query($conn,'SET NAMES utf8');

$phonenum = $_POST['PhoneNum'];

$sql= "select * from address where phonenum = '$phonenum'";
$sql1= "select * from personinfo where phonenum = '$phonenum'";
$delete_sql="delete from address where phonenum='$phonenum'";
$address_sql= "select * from address";

$response = array();
$response["inaddress"]=false;


$res= $conn->query($sql); #(address) 전송된 phonenum에 해당하는 위도 경도 값
$res1= $conn->query($sql1); #(personinfo) 전송된 phonenum의 등록 정보

if(mysqli_fetch_array($res1)!=NULL){ //등록 정보 확인
    $del_res=$conn->query($delete_sql); #(address)해당 긴급자동차의 정보를 삭제
    $response["inaddress"]=true;
}
$num=0;
$address_res= $conn->query($address_sql); #(address)모든 address 위치 정보 선택
// 위치정보 앱에 뿌려주기
while($row=mysqli_fetch_array($address_res)){
    $response["num_inaddress"]=$num;
    $numstr = (string)$num;
    $response["Latitude$numstr"]= $row["Latitude"];
    $response["Longitude$numstr"]= $row["Longitude"];
    $num++;
}

echo json_encode($response);
?>