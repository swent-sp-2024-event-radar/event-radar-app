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

<<<<<<< HEAD
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


The structure of the event database is as follows:
 ```json
{
  "Event": {
    "Name": "New Years Eve Party",
    "Photo": "https://stackoverflow.com/questions/46585330/firestore-security-rules-for-public-and-private-fields",
    "Description": "Get Ready for NYE 2025, dress classy and dance all night",
    "Category": "Party",
    "Start": {
      "Date": "31.12.2024",
      "Time": "23:00"
    },
    "End": {
      "Date": "01.01.2025",
      "Time": "06:00"
    },
    "Location": "Starling Hotel",
    "Ticket": {
      "Ticket Name": "Standard",
      "Ticket price": 20.00,
      "Ticket Quantity": 450
    },
    "Contact Email": "valerian@joytigoel.com",
    "Co-organiser List": [
      "xu378csh",
      "qwglei7"
    ] 
    
  }
}
```
/// Co-Organiser list: List of references (in Firebase).  
       
         
