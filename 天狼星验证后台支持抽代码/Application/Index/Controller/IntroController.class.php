<?php
namespace Index\Controller;
use Common\Controller\BaseController;
use Home\Tool\HJCTool;



class IntroController extends BaseController {
    public function index(){
        $this->init();
        $this->display();
    }
     private function init() {
        if (!isset($_POST['query'])) {
            return;
        }
        if ($this->isEmpty(I('post.card', '', '/^[0-9A-Z]{12}$/'))) {
            HJCTool::alertBack('授权码格式错误: 请输入12位大写英文字母或数字');
        }
        $card = I('post.card');
        $mysql = M('Regcode');
        $ret = $mysql->query("SELECT * FROM cloud_regcode WHERE code = '$card'");
        // 注册码没找到
        if (!$ret) {
             HJCTool::alertBack('授权码不存在');
        }
        $codes = array();
        array_push($codes,'查询成功\r\n');
        array_push($codes,'授权码:'.$card.'\r\n');
        array_push($codes,'时长:'.$ret[0]['time_str'].'\r\n');
        if ($ret[0]['expire_time']) {
           array_push($codes,'激活状态:已激活\r\n');
           array_push($codes,'设备串号:'.$ret[0]['computer_uid'].'\r\n');
           array_push($codes,'使用时间:'.$ret[0]['beginuse_time'].'\r\n');
           array_push($codes,'到期时间:'.$ret[0]['expire_time'].'\r\n');
           array_push($codes,'上次登录时间:'.$ret[0]['last_time'].'\r\n');
           array_push($codes,'使用次数:'.$ret[0]['use_count'].'\r\n');
        }else{
           array_push($codes,'激活状态:未激活\r\n');
        }
        // 注册码冻结
        if ($ret[0]['frozen'] == 1) {
           array_push($codes,'冻结状态:已冻结\r\n');
        }else{
           array_push($codes,'冻结状态:未冻结\r\n');
        }
        //查询成功
        HJCTool::alertBack(implode($codes));
    }
    
    
    
    
}