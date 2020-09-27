<?php
include_once('../PHPMailer/PHPMailerAutoload.php');
include_once('../amiler.lib.php');

//mailer("박지민","hiiwjd@naver.com","qkrwlals1213@naver.com","제목사과나무","내용", 1);

function mailer($fname, $fmail, $to, $subject, $content, $type=0, $file="", $cc="", $bcc="")
{
      if ($type != 1) 
          $content = nl2br($content);
      // type : text=0, html=1, text+html=2

      //$name = $_POST['name']; 
      //$emailID = $_POST['emailID']; 

      $mail = new PHPMailer(); // defaults to using php "mail()"

      $mail->IsSMTP();
         //   $mail->SMTPDebug = 2;
      $mail->SMTPSecure = "ssl";
      $mail->SMTPAuth = true;

      $mail->Host = "smtp.naver.com";
      $mail->Port = 995;
      $mail->Username = "qkrwlals1213";
      $mail->Password = "qkrwlals1!";

      $mail->CharSet = 'UTF-8';
      $mail->From = $fmail;
      $mail->FromName = $fname;
      $mail->Subject = $subject;
      $mail->AltBody = ""; // optional, comment out and test
      $mail->msgHTML($content);
      $mail->addAddress($to);
      if ($cc)
            $mail->addCC($cc);
      if ($bcc)
            $mail->addBCC($bcc);

      if ($file != "") {
            foreach ($file as $f) {
                  $mail->addAttachment($f['path'], $f['name']);
            }
      }

      return $mail->send();

      //include_once("../amiler.lib.php");
      //mailer("박지민","qkrwlals1213@naver.com","hiiwjd@naver.com","제목사과나무","내용", 1);

}

    echo "<script> location.href='tables.html'; </script>" 

?>
