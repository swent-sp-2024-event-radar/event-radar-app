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

### Events database
The structure of the event database is as follows:
 ```json
{
  "events": {
    "event_id": {
      "name": "New Years Eve Party",
      "photo_url": "https://stackoverflow.com/questions/46585330/firestore-security-rules-for-public-and-private-fields",
      "description": "Get Ready for NYE 2025, dress classy and dance all night",
      "category": "MUSIC",
      "start": "31/12/2024 23:00:00",
      "end": "01/01/2025 06:00:00",
      "location": {
        "location_name": "Starling Hotel",
        "location_lat": 19.4783892,
        "location_lng": 192.198489
      },
      "ticket": {
        "ticket_name": "Standard",
        "ticket_price": 20.00,
        "ticket_quantity": 450
      },
      "main_organiser": "xu378csh",
      "organisers_list": [
        "xu378csh",
        "qwglei7"
      ],
      "attendees_list": [
        "xu378csh",
        "qwglei7",
        "bfndn3"
      ]
    }
  }
}
```
Note:
- Co-Organiser list: List of references (in Firebase).  
       
### Messages (Chat) Database
The structure of the Messages database is as follows:
```json
{
  "messages": {
    "message_history_id": {
      "from_user": "xu378csh",
      "to_user": "qwglei7",
      "latest_message_id": "ortz34k39",
      "messages_list": [
        {
          "message_id": {
            "sender": "qwglei7",
            "content": "message here",
            "date_time_sent": "31/12/2024 23:00:00",
            "message_read": true
          }
        }
      ]
    }
  }
}
```         
Note:
- each document in `messages` holds information about a conversation between two users
