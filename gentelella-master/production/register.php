<?php
header("Content-Type: text/html; charset=UTF-8"); //UTF-8 : 한국어 사용
$conn = new mysqli("dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com","dajung","12345678","DajungDB"); //변수선언
mysqli_query($conn,'SET NAMES utf8');

    $name = $_POST['name']; //[name 값], POST형식 
    $phonenum = $_POST['phonenum'];
    $emailID = $_POST['emailID'];
    $cartype = $_POST['cartype'];


    $jb_sql = "SELECT * FROM personinfo";
    $result=mysqli_query($conn,$jb_sql);
    $phonenum1=(string)$phonenum;

    $true = 1;
    while($row=mysqli_fetch_array($result)){
        if($phonenum1==(string)$row["phonenum"]) 
        { //등록 안 됨
            $true=0;
            break;
        }
        else //등록 됨
        {
            $true = 1;
        }
    }

    if($true == 1){
            $sql= "insert into approval(name, phonenum, emailID, cartype) values('$name','$phonenum', '$emailID', '$cartype')";
            $sql2="alter table approval auto_increment=1";
            $sql3="set @count=0";
            $sql4="update approval set id=@count:=@count+1";
        
            $res=$conn->query($sql);
            $res=$conn->query($sql2);
            $res=$conn->query($sql3);
            $res=$conn->query($sql4);
    }

    if($true==0)
    {
        echo "<script>alert(\"이미 등록된 핸드폰 번호입니다. 다시 시도해주세요.\");</script>";
    }

    
    echo "<script> location.href='register.html'; </script>"

?>
