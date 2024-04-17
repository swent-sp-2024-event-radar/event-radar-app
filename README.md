# party-radar-app
Holds all the code for the Android app

TODO: Add detailed app description and demo

## Figma
[Figma Link](https://www.figma.com/file/yCDFrt0sOYFhXlYlWp8sZT/Party-Radar-App?type=design&node-id=0%3A1&mode=design&t=XbCBmVxvjFARZu1n-1)  
[Figma Dev Mode Link](https://www.figma.com/file/yCDFrt0sOYFhXlYlWp8sZT/Party-Radar-App?type=design&node-id=0%3A1&mode=dev&t=XbCBmVxvjFARZu1n-1)

## Architecture Diagram
![architecture diagram](images/architecture-diagram.png)
[Image Link](https://excalidraw.com/#json=1c_DrTFZCSGprCvJNBYHn,B0sXrISCY8YdKgiTqy9xDA)
_revised 9/4/2024_

## Backend
### Users database
The structure of the user database is as follows:
```json
{
  "users": {
    "user_id": {
      "private": {
        "private_id": {
          "age": 22,
          "email": "christineha127@gmail.com",
          "firstName": "Christine",
          "lastName": "Ha",
          "phoneNumber": "0779642510"
        }
      },
      "public": {
        "public_id": {
          "accountStatus": "active",
          "eventsAttendeeList": ["event_id1", "event_id2"],
          "eventsHostList": ["event_id1", "event_id2"],
          "profilePicUrl": "https://stackoverflow.com/questions/46585330/firestore-security-rules-for-public-and-private-fields",
          "qrCodeUrl": "https://stackoverflow.com/questions/46585330/firestore-security-rules-for-public-and-private-fields",
          "username": "chaha"
        }
      }
    }
  }
}
```
Note:
- Private and public collection created to manage permissions access using this link: https://stackoverflow.com/questions/46585330/firestore-security-rules-for-public-and-private-fields
    - Check Firestore rules for more information / details
- Images should be stored in Firebase Storage ( firebase_storage ) to upload the image file and then you store the download url of the image inside the document in the Cloud Firestore ( cloud_firestore ).