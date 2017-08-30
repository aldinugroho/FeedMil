<?php

/* Programmer : Eko Listiyono 
To change this license header, choose License Headers in Project Properties.
To change this template file, choose Tools | Templates
and open the template in the editor.
 */
header('Content-Type: application/json');
require_once 'database.php';
if( isset($_GET['GROUP']) && isset($_GET['NAME'])){
    switch ($_GET['GROUP']) {
        case "conveyor":
            $db = new DB();
            $arr = $db->getConveyor($_GET['NAME']); 
            echo json_encode($arr);
            break;
        case "crusher":
            $db = new DB();
            $arr = $db->getCrusher($_GET['NAME']);
            echo json_encode($arr);
            break;
        case "buffer":
            $db = new DB();
            $arr = $db->getBuffer($_GET['NAME']);
            echo json_encode($arr);
        default:
            break;
    }
            
}else{
    echo 'error';
}
