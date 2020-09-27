<!DOCTYPE html>
<html>
<body>
<?php header("Content-Type: text/html; charset=UTF-8"); 
session_start(); ?>
<?php if( (!isset($_SESSION['id'])) &&  (!isset($_SESSION['nickname'])) ){ ?>
<a href="join.html">회원가입</a><br>
<a href="login.html">로그인</a><br>
<?php } else {?>
<a href="logout.php">로그아웃</a><br>
<?php } ?>
<a href="board.php">게시판</a><br>
<div>
    <table>
<?php

    header("Content-Type: text/html; charset=UTF-8");

    $host = 'dajung-db.c6ivw6dubpql.ap-northeast-2.rds.amazonaws.com';

    $user = 'dajung';

    $pw = '12345678';

    $dbName = 'DajungDB';

    $conn = new mysqli($host, $user, $pw, $dbName);



    if($conn){

        echo "MySQL 접속 성공";

    }else{

        echo "MySQL 접속 실패";

    }

?>
</div>
</table>



<div>
<table><tr><th>번호</th><th>이름</th><th>휴대폰 번호</th><th>차 종류</th><th>IMEI</th></tr>
<?php 
$sql = "select *from personinfo";
$res = $conn->query($sql);

while ($row=mysqli_fetch_array($res))
{
    echo $row['id'];
    echo $row['name'];
}

?>
</table>
</div>

</body>
</html>