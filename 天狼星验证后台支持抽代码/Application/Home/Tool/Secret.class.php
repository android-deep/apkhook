<?php
namespace Home\Tool;

class Secret {
    public function createRandRegisterCode() {
        $yz = 'QWERTYUIOPASDFGHJKLZXCVBNM!@#$%^&*()_+,.?';
        $str = '';
        for ($i = 0; $i< 16; $i++) {
            $index = rand(0, strlen($yz) - 1);
            $str = $str . $yz[$index];
        }
        return substr(strtoupper(md5($str)),0,12);
    }

}