<?php

class ActivitytypeModel extends Model
{
	//获取所以分类
	public function getAllType()
	{
        $result = $this->findAll();
        //重组数据集结构
        /*$newresult = array();
        foreach ( $result as $value ){
            $newresult[$value['id']] = $value['name'];
        }*/
        return $result;
	}
        /**
         * addType 
         * 增加分类
         * @param mixed $map 
         * @access public
         * @return void
         */
     public function addType( $map ){
         if(empty($map['name'])){
             return -1;
         }
         return $this->add( $map );
     }
        /**
         * editType 
         * 编辑分类
         * @param mixed $map 
         * @access public
         * @return void
         */
        public function editType( $data ){
            $query = $this->save( $data );
            return $query;
        }
        /**
         * getTypeName 
         * 通过id获得名字
         * @param mixed $id 
         * @access public
         * @return void
         */
        public function getTypeName( $id ){
            $map['id'] = $id;
            $result = $this->where( $map )->field('name')->find();
            return $result['name'];
        }
}