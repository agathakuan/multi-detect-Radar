# multi-detect-Radar
開發
-
Alex Kuan 2019/3/22 v1

已有功能
-
1. 可調整system notificaiton 
2. 添加裝置前後皆可調整 thershold
3. 可指定notify speed/ direction **尚未加入doppler freq thershold**
4. 可指定配對房間名稱
5. 最多可指定四個不同的房間
6. 採**輪詢機制**(12sec)輪流BLE Connect各個裝置
7. 目前已接上真正的doppler radar 硬體，並修改為對應 data format
8. 對應 MCU firmware ：Radar_doppler_ble_v0.hex