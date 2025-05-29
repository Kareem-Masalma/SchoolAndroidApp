<?php
header("Content-Type: application/json");
require_once "db.inc.php";

if ($_SERVER["REQUEST_METHOD"] === "POST") {
  $data = json_decode(file_get_contents("php://input"), true);

  $user_id = $data["user_id"] ?? "";
  $password = $data["password"] ?? "";

  if (!$user_id || !$password) {
    echo json_encode(["success" => false, "message" => "Missing credentials"]);
    exit;
  }

  $stmt = $conn->prepare("SELECT * FROM users WHERE user_id = ?");
  $stmt->bind_param("s", $user_id);
  $stmt->execute();
  $result = $stmt->get_result();

  if ($user = $result->fetch_assoc()) {
    if ($user["password"] === $password) {
      unset($user["password"]);
      echo json_encode(["success" => true, "user" => $user]);
    } else {
      echo json_encode(["success" => false, "message" => "User ID or Password"]);
    }
  } else {
    echo json_encode(["success" => false, "message" => "User ID or Password"]);
  }

  $stmt->close();
  $conn->close();
} else {
  echo json_encode(["success" => false, "message" => "Invalid request"]);
}
