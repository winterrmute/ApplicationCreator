# ApplicationCreator
This program is still in the process of making. If you wish to creates an application from Latex template, follow these steps:

- under src/test/resources you will find data.json. This is an example file which contains the data structure needed to create application.
- copy data.json to src/main/resources and fill it with your content.
- under src/main/resources create subdirectory pics: Put there two files:
	- picture of you and name it "pic.jpg"
	- picture of your signature and name it "signature.jpg"
- run the programm.

Under src/main/texTemplate you will find two new files. "coverLetter.tex" and "cv.tex". Now open it in your favorite tex editor and execute it as xeLaTex. Your generated pdfs you will find in the same directory.