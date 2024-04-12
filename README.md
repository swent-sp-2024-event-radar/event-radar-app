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
      "qwglei7" #
    ] 
    
  }
}
   ```
/// Co-Organiser list: List of references (in Firebase).  
       
         