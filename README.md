# TODAY_WRITE
내가 쓸려고 만들었는데 만들고 보니 나는 ios 유저였다
***

## 1. TODAY WRITE
### Android 일기장&기분 통계 앱
안드로이드 버전으로 만든 일기장 앱. 내가 쓸려고 만들었지만 생각해보니 ios 유저라서 못쓰게 되었고 공기계에서만 쓰는 슬픈 사실 \
날씨와 위치정보를 받아와서 자동으로 기록해주고 요일 별 기분과 비율을 통계 도표로 시각적으로 제시\
총 2주 정도 걸린 것 같은데 깃헙 commit이랑 push 가 귀찮아서 안했더니 처음이자 마지막 commit&push 가 되었다\
* Android Studio API 30
* Weather API xml parsing
* GPS location
* SQLiteDatabase
* 그림판 (무려 직접 디자인...)
  

  

## 2. Screenshots
<img src="https://user-images.githubusercontent.com/63590121/132048712-8a3a980a-9fab-42b4-81fd-ad8189706d2a.gif" width=400 height=700> <img src="https://user-images.githubusercontent.com/63590121/132048772-b110e705-76b9-4d14-9aed-7b5938438139.gif" width=400 height=700> 

<img src="https://user-images.githubusercontent.com/63590121/132049593-41cd86ed-cc7c-422c-af32-5ce388f1a57d.png" width=200 height=360> <img src="https://user-images.githubusercontent.com/63590121/132049588-f51d1261-3ca1-4a36-9455-45c0eb022aff.png" width=200 height=360>
<img src="https://user-images.githubusercontent.com/63590121/132049591-3c6ab7c7-3795-40a8-9a67-ccde733b8ac3.png" width=200 height=360> <img src="https://user-images.githubusercontent.com/63590121/132049592-adeb2c63-0ed0-4a3c-ae71-a9794489a707.png" width=200 height=360> 

    
    
## 3. NO DESIGNER PROJECT
  
<img width="600" alt="캡처" src="https://user-images.githubusercontent.com/63590121/132050329-52857fda-c8e8-4478-a851-5ee60b3595ef.png">
처음에는 진짜 그림판으로 대충 그렸는데 돌아온 반응이 싸늘해서 다시 그림판으로 그렸다 일러스트 못쓰고 디자인 못하는 나는 이게 최선이다 
  
근데 사실 그렇게 이상하다고 느끼지 않는다 
  
<img width="400" alt="캡처" src="https://user-images.githubusercontent.com/63590121/132050188-051e2adb-b8ce-46a1-8d6a-0f97c24fc593.PNG">

## 4. How To Use
app\release 에 보면 TODAY_WRITE.apk 가 있다\
https://github.com/josushell/TODAY_WRITE/tree/master/app/release \
사실 플레이 스토어에 올리려고 했으나 이대로 올렸다간 사람들에게 비난당하기 쉽상이기 때문에 포기했다\
<img width="238" alt="sample" src="https://user-images.githubusercontent.com/63590121/132050253-a3e7a1a3-b874-44d2-b649-d9b98a783fc9.png">  

## 5. RETROSPECT
내 노트북이 안드로이드 스튜디오 때문에 블루스크린 수집가가 되었는데 역시 노트북으로 AVD 돌리는 것은 쉽지가 않다 그리고 진짜 디버깅에만 며칠씩 쓴듯\
마지막에는 비동기 AyncTask로 처리한 날씨 api parsing에서 자꾸 원인 모를 오류로 강제 종료되서 이걸 해결한다고 고생 좀 했다\
안드로이드 스튜디오는 겨울에 난로 없을 때 딱 쓰기 좋다... ios 버전으로도 만들어야지
