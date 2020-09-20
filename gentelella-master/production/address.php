<?php
header("Content-Type: text/html; charset=UTF-8");
$conn = new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com","dajung","12345678","DajungDB");
mysqli_query($conn,'SET NAMES utf8');

$latitude = $_POST['Latitude'];
$longitude = $_POST['Longitude'];
$phonenum = $_POST['PhoneNum'];
$emg_button = $_POST['emg_button'];

$sql= "select * from address where phonenum = '$phonenum'";
$sql1= "select * from personinfo where phonenum = '$phonenum'";
$sql2= "insert into address(phonenum, Latitude, Longitude) values('$phonenum', '$latitude','$longitude')";
$sql3= "select * from address";
$sql_update1 = "update address set Latitude='$latitude' where phonenum='$phonenum'";
$sql_update2 = "update address set Longitude='$longitude' where phonenum='$phonenum'";
$sql_delete = "delete from address where phonenum='$phonenum'";


$response = array();
$response["emergency"]=false;
$response["number"]=0;
$response["Latitude0"]='0';
$response["Longitude0"]='0';


$res= $conn->query($sql);
$res1= $conn->query($sql1);

if(mysqli_fetch_array($res1)!=NULL)
{
    //긴급자동차 등록된 사람이라면
    $response["emergency"]=true;
    if($emg_button==1)
    {
        if(mysqli_fetch_array($res)==NULL){
            $res2= $conn->query($sql2);
        }
        else{
            $res_lat= $conn->query($sql_update1);
            $res_lng= $conn->query($sql_update2);
        }
    }
    else if(mysqli_fetch_array($res)!=NULL && $emg_button==0)
    {
        $res_delete = $conn->query($sql_delete);
    }
}

$num=0;

$res3= $conn->query($sql3);
// 위치정보를 마커로 앱에 표시하기 위함
while($row=mysqli_fetch_array($res3)){
    $response["number"]=$num;
    $numstr = (string)$num;
    $response["Latitude$numstr"]= $row["Latitude"];
    $response["Longitude$numstr"]= $row["Longitude"];
    $num++;
}

echo json_encode($response);
?>