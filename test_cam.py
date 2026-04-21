import cv2
cap = cv2.VideoCapture(0)
print("Opened:", cap.isOpened())
ret, frame = cap.read()
print("Frame read:", ret)
if ret:
    cv2.imshow("Test", frame)
    cv2.waitKey(3000)
cap.release()
cv2.destroyAllWindows()