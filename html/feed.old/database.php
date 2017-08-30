<?php

/* Programmer : Eko Listiyono 
  To change this license header, choose License Headers in Project Properties.
  To change this template file, choose Tools | Templates
  and open the template in the editor.
 */

class DB {

    function connectDatabase() {
        $dbh = new PDO('mysql:host=localhost;dbname=feed_mill', "root", "");
        return $dbh;
    }

    function getConveyor($name) {
        $koneksi = $this->connectDatabase();
        $query = "SELECT * FROM machine_description md inner join conv_status cs on md.machine_id=cs.machine_id "
                . " where md.name = ? ";
        $result = $koneksi->prepare($query);
        $result->execute(array($name));
        
        RETURN $result->fetchAll(PDO::FETCH_ASSOC);
    }
    function getCrusher($name){
        $koneksi = $this->connectDatabase();
        $query = "SELECT * FROM machine_description md inner join crusher_status cs on md.machine_id=cs.machine_id "
                . " where md.name = ? ";
        $result = $koneksi->prepare($query);
        $result->execute(array($name));
        
        RETURN $result->fetchAll(PDO::FETCH_ASSOC);
    }
    function getBuffer($name){
        $koneksi = $this->connectDatabase();
        $query = "SELECT * FROM machine_description md inner join buffer_status bs on md.machine_id=bs.machine_id "
                . " where md.name = ? ";
        $result = $koneksi->prepare($query);
        $result->execute(array($name));
        
        RETURN $result->fetchAll(PDO::FETCH_ASSOC);
    }

}
