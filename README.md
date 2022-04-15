# Emergency Vehicle Notification Service : web
[2020] Emergency Vehicle Notification Service : web code 
_Update: 2022-04-13_  
## **Index**
+ [About this project](#about-this-project)
+ [Overview](#overview)
  + [Goal](#goal)
  + [Flow](#flow)
+ [Detail Function](#detail-function)    
  + [Address](#1-addressphp--위치-비교)
  + [TurntoNormal](#2-turntonormalphp--긴급자동차-운전자가-일반자동차-운전자로-변경)
    
+ [Environment](#environment)

## **About this project**
<img src = "https://user-images.githubusercontent.com/68631435/163201142-024fc295-7b25-4a72-b2cc-52f14154a010.png" width="60%" height="40%">   

+ 프로젝트 이름: 긴급자동차 위치 알림 서비스
+ 프로젝트 진행 목적: ICT 멘토링 및 공모전 출전    
▻ (이브와 ICT 멘토링) 참고 url: https://www.hanium.or.kr/portal/kibwa/businessOverview.do  
+ 프로젝트 진행 기간: 2020년 5월 ~ 2020년 11월  
+ 프로젝트 참여 인원: 5명  
## **Overview** 
> ### **Goal**
+ (목적) 운전자에게 긴급자동차의 접근을 알림으로서 긴급자동차 경로 확보를 도우고 2차 사고 발생을 예방하기 위함.  
+ (필요성) 운전자의 시야 확보가 불가능한 상황에서 긴급자동차의 위치를 실시간으로 알려 신속하고 원활한 이동을 도움.   
> ### **Flow**
<img src = "https://user-images.githubusercontent.com/68631435/163202194-e6f579cf-888a-45f1-9d44-0b9036d7d5e8.png" width="45%" height="45%">   
<img src = "https://user-images.githubusercontent.com/68631435/163204774-367f1ab7-9e89-4b02-bd14-79ec5ee89c84.png" width="50%" height="height 20%">    

## **Detail Function**
> ### **Server**   
> 본 프로젝트의핵심 기능을 구현한 코드를 설명하였음.     
#### **(1) address.php : 위치 비교**   
#### path: \gentelella-master\production\ address.php
``` php
$latitude = $_POST['Latitude'];
$longitude = $_POST['Longitude'];
$phonenum = $_POST['PhoneNum'];
$emg_button = $_POST['emg_button'];

$response = array();
$response["emergency"]=false;
$response["number"]=0;
$response["Latitude0"]='0';
$response["Longitude0"]='0';

$sql= "select * from address where phonenum = '$phonenum'";
$res= $conn->query($sql); // 운전자의 위치 정보

$sql1= "select * from personinfo where phonenum = '$phonenum'";
$res1= $conn->query($sql1); // 등록된 운전자인지 확인

if(mysqli_fetch_array($res1)!=NULL) // 등록된 운전자라면
{
    
    $response["emergency"]=true;
    if($emg_button==1)
    {
         //긴급자동차 운전자의 경우
        if(mysqli_fetch_array($res)==NULL){
            // 운전자의 위치값이 없는 경우 
            $sql2= "insert into address(phonenum, Latitude, Longitude) values('$phonenum', '$latitude','$longitude')";
            $res2= $conn->query($sql2); //운전자 위치 갱신(insert)
        }
        else{
            //운전자의 위치값 갱신 (update)
            $sql_update1 = "update address set Latitude='$latitude' where phonenum='$phonenum'";
            $sql_update2 = "update address set Longitude='$longitude' where phonenum='$phonenum'";

            $res_lat= $conn->query($sql_update1);
            $res_lng= $conn->query($sql_update2);
        }
    }
    else if(mysqli_fetch_array($res)!=NULL && $emg_button==0)
    {
        //일반 운전자
        $sql_delete = "delete from address where phonenum='$phonenum'";
        $res_delete = $conn->query($sql_delete); // 위치정보 삭제 -> 저장할 필요 없음
    }
}

$num=0;

// 형변환
$latitude_temp = (double)$latitude;
$longitude_temp = (double)$longitude;

$sql3= "select * from address where Latitude<$latitude_temp+0.005 and Latitude>$latitude_temp-0.005 and Longitude<$longitude_temp+0.005 and Longitude>$longitude_temp-0.005";
$res3= $conn->query($sql3); //긴급자동차 운전자와 일정 범위 안에 있는 운전자 정보 수집 

// 긴급자동차의 위치정보 배열 생성 
while($row=mysqli_fetch_array($res3)){
    $response["number"]=$num;
    $numstr = (string)$num;
    $response["Latitude$numstr"]= $row["Latitude"];
    $response["Longitude$numstr"]= $row["Longitude"];
    $num++;
}

echo json_encode($response);

``` 
+ (1) Client(운전자어플)에서 Server로 계속 자신의 위치정보(위도, 경도) 값을 전송함. 
+ (2) address에 저장된 긴급자동차 운전자 위치와 비교 후, 일정 범위 내에 겹치면 해당 긴급자동차 운전자의 위치 배열을 생성함. 
    + address는 긴급자동차 운전자의 위치 정보를 관리하는 테이블임. 
+ (3) 생성된 배열을 Client에 전송함.   

#### **(2) turntonormal.php : 긴급자동차 운전자가 일반자동차 운전자로 변경**   
#### path : \gentelella-master\production\turntonormal.php  
``` php
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
``` 
+ Client(운전자 어플)에서 긴급자동차 운전자가 일반자동차 운전자로 바꾸는 경우,   
+ (1) 긴급자동차 운전자로 등록되었던 운전자의 위치정보 삭제함. 
+ (2) address 테이블에 있는 모든 긴급자동차 운전자의 위치정보를 Client에게 전송함. 

## **Environment** 
+ EC2(AWS): Amazon Linux release 2  
+ Putty : 리눅스 서버 원격 접속용 툴  
+ Apache(2.4.43) : 관리자 웹 페이지를 구동하는 웹 서버 프로그램  
+ Maria DB : 단말기 데이터를 저장 및 관리하는 데이터베이스 시스템  
+ PHP(5.4.16) : 서버 관리자 웹 페이지 처리 모듈 작성 언어  
+ Visual Studio Code : 웹 서버 HTML, CSS, Javascript, PHP 개발
