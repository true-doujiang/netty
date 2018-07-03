#营销运营后台服务接口文档

		作者 | 邮箱 |编写时间|备注
		--- | ---  | ---	  | ---
		丁宁 | dingl@diandainfo.com | 2018-05-31|创建文档(初版v1.0)
	   丁宁 | dingl@diandainfo.com | 2018-05-31|创建文档(修订版v1.1)

#### 引言
	1：协议： http
	2：域名详情
			DEV域名： markting-dev。denghuier.com
			PL域名：  markting-pl.denghuier.com
			Online域名：markting.denghuier.com
	3: 遵守restful风格协议
	
## 1.活动类型(代金券)列表
### 接口说明
	获取所有活动
### 请求方式
	GET
### 接口地址
	url: /api/campaign
### 请求参数
	无
### 响应参数
	{
    "code": 0,
    "data": [
        {
            "beginTime": "2018-05-22 13:00:00",
            "consumerCount": 0,
            "consumerLimitType": 1,
            "couponType": 2,
            "createTime": "2018-05-23 08:19:33",
            "endTime": "2018-05-22 13:00:00",
            "giftName": "",
            "id": 47,
            "issueCount": 99,
            "level": 1,
            "name": "测试无门槛¥18.88",
            "price": 1888,
            "receiveType": 2,
            "status": 1,
            "updateTime": "2018-05-23 08:27:13",
            "useRules": "• 活动时间：2018.06.01 00:00 - 2018.06.30 00:00\n• 本次活动中一个红包完全开启才能开启下一个红包；\n• 每个用户ID每天仅限累计可开启3张不同额度的红包；",
            "usedCount": 0,
            "userAllCount": 1,
            "userDayCount": 1,
            "validBeginTime": "2018-05-22 13:00:00",
            "validEndTime": "2018-06-21 13:00:00",
            "validHour": 24,
            "validTimeType": 1,
            "weight": 99
        }
    ],
    "message": "处理成功",
    "timestamp": 1527735814970
}	

## 2.根据ID查询活动信息
### 接口说明
	根据id查询活动信息
### 请求方式
	GET
### 接口地址
	/api/campaign/${id}
### 请求参数
	${id}
### 响应参数
	{
	    "code": 0,
	    "data": {
	        "beginTime": "2018-05-22 13:00:00",
	        "brokenCount": 2,
	        "consumerCount": 0,
	        "consumerLimitType": 1,
	        "couponType": 1,
	        "createTime": "2018-05-23 08:11:27",
	        "endTime": "2018-05-22 13:00:00",
	        "giftName": "",
	        "id": 41,
	        "isDelete": 0,
	        "issueCount": 99,
	        "level": 1,
	        "name": "测试红包¥18.88",
	        "price": 1888,
	        "receiveType": 2,
	        "status": 1,
	        "updateTime": "2018-06-01 04:38:00",
	        "useRules": "• 活动时间：2018.06.01 00:00 - 2018.06.30 00:00\n• 本次活动中一个红包完全开启才能开启下一个红包；\n• 每个用户ID每天仅限累计可开启3张不同额度的红包；",
	        "usedCount": 7,
	        "userAllCount": 99,
	        "userDayCount": 2,
	        "validBeginTime": "2018-05-22 13:00:00",
	        "validEndTime": "2018-06-21 13:00:00",
	        "validHour": 24,
	        "validTimeType": 1,
	        "weight": 99
	    },
	    "message": "处理成功",
	    "timestamp": 1527753683946
	}

## 3. 分页查询活动信息
### 接口说明
	分页查询活动信息
### 请求方式
	GET
### 接口地址
	/api/campaign/lsit
### 请求参数
	name
	couponType
	beginTime
	endTime
### 响应参数
	{
    "code": 0,
    "data": {
        "asc": true,
        "current": 1,
        "limit": 10,
        "offset": 0,
        "offsetCurrent": 0,
        "openSort": true,
        "pages": 0,
        "records": [
            {
                "beginTime": "2018-05-22 13:00:00",
                "brokenCount": 2,
                "consumerCount": 0,
                "consumerLimitType": 1,
                "couponType": 1,
                "createTime": "2018-05-23 08:11:27",
                "endTime": "2018-05-22 13:00:00",
                "giftName": "",
                "id": 41,
                "isDelete": 0,
                "issueCount": 99,
                "level": 1,
                "name": "测试红包¥18.88",
                "price": 1888,
                "receiveType": 2,
                "status": 1,
                "updateTime": "2018-06-01 04:38:00",
                "useRules": "• 活动时间：2018.06.01 00:00 - 2018.06.30 00:00\n• 本次活动中一个红包完全开启才能开启下一个红包；\n• 每个用户ID每天仅限累计可开启3张不同额度的红包；",
                "usedCount": 7,
                "userAllCount": 99,
                "userDayCount": 2,
                "validBeginTime": "2018-05-22 13:00:00",
                "validEndTime": "2018-06-21 13:00:00",
                "validHour": 24,
                "validTimeType": 1,
                "weight": 99
            }
        ],
        "searchCount": true,
        "size": 10,
        "total": 0
    },
    "message": "处理成功",
    "timestamp": 1527753988744
}

## 5. 修改活动信息
### 接口说明
	修改活动信息
### 请求方式
	GPUT
### 接口地址
	/api/campaign
### 请求参数
	id
	name
	validBeginTime
	validEndTime
	reveiveType
	userDayCount
	userAllCount
	couponType
	price
	giftName
	level
	brokenCount
	validTimeType
	validHour
	beginTime
	endTime
	issueCount
	userdCount
	weight
	useTules
	consumerLimitType
	consumerCount
	
### 响应参数
	{
    "code": 0,
    "message": "处理成功!",
    "timestamp": 1527754526668
	}
	
## 6. 保存活动信息
### 接口说明
	保存活动信息
### 请求方式
	POST
### 接口地址
	/api/campaign
### 请求参数
	name
	validBeginTime
	validEndTime
	reveiveType
	userDayCount
	userAllCount
	couponType
	price
	giftName
	level
	brokenCount
	validTimeType
	validHour
	beginTime
	endTime
	issueCount
	userdCount
	weight
	useTules
	consumerLimitType
	consumerCount

### 响应参数
	{
    "code": 0,
    "message": "处理成功!",
    "timestamp": 1527754526668
	}

## 7. 查询是否有在进行中的活动    
### 接口说明
	查询是否有在进行中的活动
### 请求方式
	GET
### 接口地址
	/api/campaign/status
### 请求参数
	无
### 响应参数
	{
    "code": 0,
    "data": {
        "status": true
    },
    "message": "处理成功",
    "timestamp": 1527755596410
	}
## 8.根据状态获取当前用户的领取红包记录
### 接口说明
	根据状态获取当前用户的领取红包记录
### 请求方式
	GET
### 接口地址
	/api/receiveLogs
### 请求参数
	status
### 响应参数
	{
    "code": 0,
    "data": [
        {
            "brokenCount": 2,
            "campaignDto": {
                "beginTime": "2018-05-22 13:00:00",
                "brokenCount": 2,
                "consumerCount": 0,
                "consumerLimitType": 1,
                "couponType": 1,
                "createTime": "2018-05-23 08:11:27",
                "endTime": "2018-05-22 13:00:00",
                "giftName": "",
                "id": 41,
                "isDelete": 0,
                "issueCount": 99,
                "level": 1,
                "name": "测试红包¥18.88",
                "price": 1888,
                "receiveType": 2,
                "status": 1,
                "updateTime": "2018-06-01 05:27:43",
                "useRules": "• 活动时间：2018.06.01 00:00 - 2018.06.30 00:00\n• ",
                "usedCount": 8,
                "userAllCount": 99,
                "userDayCount": 2,
                "validBeginTime": "2018-05-22 13:00:00",
                "validEndTime": "2018-06-21 13:00:00",
                "validHour": 24,
                "validTimeType": 1,
                "weight": 99
            },
            "campaignId": 41,
            "consumerLimitType": 1,
            "createTime": "2018-06-01 05:34:38",
            "getTime": "2018-05-31 16:34:39",
            "id": 112,
            "status": 2,
            "updateTime": "2018-06-01 05:34:51",
            "userId": 22
        }
    ],
    "message": "处理成功",
    "timestamp": 1527756066929
	}

## 9.分页查询领取记录
### 接口说明
	分页查询领取记录
### 请求方式
	GET
### 接口地址
	/api/receiveLogs/pageList
### 请求参数
	nickName
	mobile
	city
### 响应参数
	{
    "code": 0,
    "data": {
        "asc": true,
        "current": 1,
        "limit": 10,
        "offset": 0,
        "offsetCurrent": 0,
        "openSort": true,
        "pages": 0,
        "records": [
            {
                "campaignId": 41,
                "consumerLimitType": 1,
                "createTime": "2018-06-01 05:30:41",
                "getTime": "2018-05-25 16:30:41",
                "id": 109,
                "status": 4,
                "updateTime": "2018-06-01 05:34:11",
                "userId": 22
            }
        ],
        "searchCount": true,
        "size": 10,
        "total": 0
    },
    "message": "处理成功",
    "timestamp": 1527756520130
	}

## 10.根据ID查询领取记录
### 接口说明
	根据ID查询领取记录
### 请求方式
	GET
### 接口地址
	/api/receiveLogs/${id}
### 请求参数
	${id}
### 响应参数
	{
	    "code": 0,
	    "data": {
	        "campaignId": 41,
	        "consumerLimitType": 1,
	        "createTime": "2018-06-01 05:34:38",
	        "getTime": "2018-05-31 16:34:39",
	        "id": 112,
	        "status": 2,
	        "updateTime": "2018-06-01 05:34:51",
	        "userId": 22
	    },
	    "message": "处理成功",
	    "timestamp": 1527756767146
	}
## 11.convert
### 接口说明
	convert
### 请求方式
	GET
### 接口地址
	/api/receiveLogs/convert
### 请求参数
	无
### 响应参数
	{
    "code": 0,
    "data": {
        "batches": [
            "闹够了、没有",
            "撞入你怀",
            "以后的以后 拿命爱自己",
            "泪再咸没有海水咸",
            
        ],
        "count": 6633
    },
    "message": "处理成功",
    "timestamp": 1527757209338
	}
## 12.领取活动卡券
### 接口说明
	领取活动卡券
### 请求方式
	POST
### 接口地址
	/api/receiveLogs
### 请求参数
	无
### 响应参数
	{
    "code": 0,
    "data": {
        "campaignDto": {
            "beginTime": "2018-05-22 13:00:00",
            "brokenCount": 2,
            "consumerCount": 0,
            "consumerLimitType": 1,
            "couponType": 1,
            "createTime": "2018-05-23 08:11:27",
            "endTime": "2018-05-22 13:00:00",
            "giftName": "",
            "id": 41,
            "isDelete": 0,
            "issueCount": 99,
            "level": 1,
            "name": "测试红包¥18.88",
            "price": 1888,
            "receiveType": 2,
            "status": 1,
            "updateTime": "2018-06-01 06:05:27",
            "useRules": "• 活动时间：2018.06.01 00:00 - 2018.06.30 00:00\n• ",
            "usedCount": 9,
            "userAllCount": 99,
            "userDayCount": 2,
            "validBeginTime": "2018-05-22 13:00:00",
            "validEndTime": "2018-06-21 13:00:00",
            "validHour": 24,
            "validTimeType": 1,
            "weight": 99
        },
        "campaignId": 41,
        "consumerLimitType": 1,
        "getTime": "2018-05-31 17:21:08",
        "id": 115,
        "status": 1,
        "userId": 27
    },
    "message": "处理成功",
    "timestamp": 1527758469443
	}
## 13.分页查询拆券记录
### 接口说明
	分页查询拆券记录
### 请求方式
	GET
### 接口地址
	/api/brokenLogs
### 请求参数
	niceName
### 响应参数
	{
    "code": 0,
    "data": {
        "asc": true,
        "current": 1,
        "limit": 10,
        "offset": 0,
        "offsetCurrent": 0,
        "openSort": true,
        "pages": 0,
        "records": [
            {
                "brokenTime": "2018-05-31 16:31:10",
                "campaignId": 41,
                "createTime": "2018-06-01 05:31:09",
                "id": 116,
                "niceName": "classloader",
                "receiveLogsId": 109,
                "updateTime": "2018-06-01 05:31:09",
                "userId": 27
            }
        ],
        "searchCount": true,
        "size": 10,
        "total": 0
    },
    "message": "处理成功",
    "timestamp": 1527758915618
	}
## 14.根据记录id 查询拆券记录
### 接口说明
	根据记录id 查询拆券记录
### 请求方式
	GET
### 接口地址
	/api/brokenLogs/list/${id}
### 请求参数
	${id}
### 响应参数
	{
    "code": 0,
    "data": [
        {
            "brokenTime": "2018-05-31 17:04:54",
            "campaignId": 41,
            "createTime": "2018-06-01 06:04:54",
            "headIcon": "https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKKDibPR3Tm1rp1ibija30qA/132",
            "id": 124,
            "niceName": "小迷糊",
            "receiveLogsId": 113,
            "updateTime": "2018-06-01 06:04:54",
            "userId": 24
        }
    ],
    "message": "处理成功",
    "timestamp": 1527759336695
	}
	
## 15.保存拆券记录
### 接口说明
	保存拆券记录
### 请求方式
	POST
### 接口地址
	/api/brokenLogs
### 请求参数
	{
		"receiveLogsId": "113"
	}
### 响应参数
	{
    "code": 200003,
    "message": "红包已拆完",
    "timestamp": 1527761827162
	}
	
## 16.分页查询用户卡券信息
### 接口说明
	保存拆券记录
### 请求方式
	GET
### 接口地址
	/api/userCoupon/list
### 请求参数
	getType
### 响应参数
	{
    "code": 0,
    "data": {
        "asc": true,
        "current": 1,
        "limit": 10,
        "offset": 0,
        "offsetCurrent": 0,
        "openSort": true,
        "pages": 0,
        "records": [
            {
                "campaignId": 48,
                "couponCode": "B38.88card36DB900001",
                "couponId": 1805,
                "getTime": "2018-05-31 16:31:36",
                "getType": 1,
                "id": 60,
                "receiveId": 110,
                "status": 0,
                "userId": 22
            }
        ],
        "searchCount": true,
        "size": 10,
        "total": 0
    },
    "message": "处理成功",
    "timestamp": 1527762720679
	}
	
## 17.根据ID查询用户卡券信息
### 接口说明
	根据ID查询用户卡券信息
### 请求方式
	GET
### 接口地址
	/api/userCoupon/${id}
### 请求参数
	${id}
### 响应参数
	{
    "code": 0,
    "data": {
        "campaignDto": {
            "beginTime": "2018-05-22 13:00:00",
            "consumerCount": 0,
            "consumerLimitType": 1,
            "couponType": 2,
            "createTime": "2018-05-23 08:20:00",
            "endTime": "2018-05-22 13:00:00",
            "giftName": "",
            "id": 48,
            "isDelete": 0,
            "issueCount": 99,
            "level": 2,
            "name": "测试无门槛¥38.88",
            "price": 3888,
            "receiveType": 2,
            "status": 1,
            "updateTime": "2018-05-23 08:27:06",
            "useRules": "• 活动时间：2018.06.01 00:00 - 2018.06.30 00:00\n• ",
            "usedCount": 0,
            "userAllCount": 1,
            "userDayCount": 1,
            "validBeginTime": "2018-05-22 13:00:00",
            "validEndTime": "2018-06-21 13:00:00",
            "validHour": 24,
            "validTimeType": 1,
            "weight": 99
        },
        "campaignId": 48,
        "couponCode": "B38.88card36DB900001",
        "couponId": 1805,
        "getTime": "2018-05-31 16:31:36",
        "getType": 1,
        "id": 60,
        "receiveId": 110,
        "receiveLogsDto": {
            "campaignId": 42,
            "consumerLimitType": 1,
            "createTime": "2018-06-01 05:31:15",
            "getTime": "2018-05-25 16:31:15",
            "id": 110,
            "status": 3,
            "updateTime": "2018-06-01 05:33:41",
            "userId": 22
        },
        "status": 0,
        "userId": 22
    },
    "message": "处理成功",
    "timestamp": 1527762862174
	}

## 18.根据用户ID和status获取卡券
### 接口说明
	根据用户ID和status获取卡券
### 请求方式
	GET
### 接口地址
	/api/userCoupon
### 请求参数
	status
### 响应参数
	{
    "code": 0,
    "data": [
        {
            "campaignDto": {
                "beginTime": "2018-05-22 13:00:00",
                "consumerCount": 0,
                "consumerLimitType": 1,
                "couponType": 2,
                "createTime": "2018-05-23 08:20:22",
                "endTime": "2018-05-22 13:00:00",
                "giftName": "",
                "id": 49,
                "isDelete": 0,
                "issueCount": 99,
                "level": 3,
                "name": "测试无门槛¥58.88",
                "price": 5888,
                "receiveType": 2,
                "status": 1,
                "updateTime": "2018-05-23 08:27:05",
                "useRules": "• 活动时间：2018.06.01 00:00 - 2018.06.30 00:00\n•",
                "usedCount": 0,
                "userAllCount": 1,
                "userDayCount": 1,
                "validBeginTime": "2018-05-22 13:00:00",
                "validEndTime": "2018-06-21 13:00:00",
                "validHour": 24,
                "validTimeType": 1,
                "weight": 99
            },
            "campaignId": 49,
            "couponCode": "BcardB336B000001",
            "couponId": 1810,
            "getTime": "2018-05-31 18:30:58",
            "getType": 1,
            "id": 63,
            "receiveId": 122,
            "status": 0,
            "userId": 27
        }
    ],
    "message": "处理成功",
    "timestamp": 1527764653139
}


	