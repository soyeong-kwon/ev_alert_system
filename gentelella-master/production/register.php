<?php
header("Content-Type: text/html; charset=UTF-8");
$conn = new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com","dajung","12345678","DajungDB");
mysqli_query($conn,'SET NAMES utf8');

    $name = $_POST['name'];
    $phonenum = $_POST['phonenum'];
    $cartype = $_POST['cartype'];

    $sql= "insert into personinfo(name, phonenum, cartype) values('$name','$phonenum','$cartype')";
    $res= $conn->query($sql);
    
    echo "<script> location.href='register.html'; </script>"

?>
