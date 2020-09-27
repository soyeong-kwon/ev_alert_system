<?php
header("Content-Type: text/html; charset=UTF-8"); //UTF-8 : 한국어 사용
$conn = new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com","dajung","12345678","DajungDB"); //변수선언
mysqli_query($conn,'SET NAMES utf8');

    $name = $_POST['name']; //[name 값], POST형식 
    $phonenum = $_POST['phonenum'];
    $emailID = $_POST['emailID'];
    $cartype = $_POST['cartype'];

<<<<<<< HEAD
    $sql= "insert into personinfo(name, phonenum, emailID, cartype) values('$name','$phonenum', '$emailID', '$cartype')";
    // personinfo : DB, db에 값을 넣어주는 명령어 sql언어
    $res= $conn->query($sql); //query문으로 명령어 실행
    
    echo "<script> location.href='register.html'; </script>" // 자바스크립트 언어 : html로 넘어갑니다
=======
    $sql= "insert into personinfo(name, phonenum, cartype) values('$name','$phonenum','$cartype')";
    $sql2="alter table personinfo auto_increment=1";
    $sql3="set @count=0";
    $sql4="update personinfo set id=@count:=@count+1";
    
    $res=$conn->query($sql);
    $res=$conn->query($sql2);
    $res=$conn->query($sql3);
    $res=$conn->query($sql4);

    echo "<script> location.href='register.html'; </script>"
>>>>>>> 2b7fa438985b4a8b61ca565c3ff99fac427e8b03

?>
