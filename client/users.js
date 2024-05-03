var request = require("request")

const serverAddress = "http://127.0.0.1:9090";

function loginRequest(email, password) {
  const loginUrl = serverAddress + "/login";
  const loginData = {
    email,
    password
  };

  return new Promise((resolve, reject) => {
    function handleResponse(error, response, body) {
      if (!error && response.statusCode === 200) {
        resolve([response.headers['authorization'], response.body]);
      } else {
        reject([response.statusCode, response.body]);
      }
    }

    request({url: loginUrl, method: "POST", json: loginData}, handleResponse);
  });
}

function sendAuthorizedRequest(url, method, json, token, callback) {
  request({
    headers: {"Authorization": token},
    url: url,
    method: method,
    json: json
  }, callback);
}

function registrationRequest(userRole, registerData) {
  return new Promise((resolve, reject) => {
    function handleResponse(error, response, body) {
      if (error) {
        reject(error)
      } else if (response.statusCode === 201) {
        resolve(response.body);
      } else {
        reject([response.statusCode, response.body]);
      }
    }

    request({url: `${serverAddress}/register/${userRole}`, method: "POST", json: registerData}, handleResponse);
  });
}

function accountInformationRetrievalRequest(userRole, userId, token) {
  return new Promise((resolve, reject) => {
    function handleResponse(error, response, body) {
      if (!error && response.statusCode === 200) {
        resolve(response.body);
      } else {
        reject([response.statusCode, response.body]);
      }
    }

    sendAuthorizedRequest(`${serverAddress}/get-${userRole}-account/${userId}`, "GET", {}, token, handleResponse);
  })
}

function accountInformationUpdateRequest(userRole, userId, data, token) {
  return new Promise((resolve, reject) => {
    function handleResponse(error, response, body) {
      if (!error && response.statusCode === 200) {
        resolve(response.body);
      } else {
        reject([response.statusCode, response.body]);
      }
    }

    sendAuthorizedRequest(`${serverAddress}/update-${userRole}-account/${userId}`, "PUT", data, token, handleResponse);
  })
}

function registerStudent(studentNumber, birthDate, name, surname, email, password) {
  return registrationRequest("student", {
      studentNumber,
      birthDate,
      name,
      surname,
      email,
      password
  });
}


function registerFirm(registerDate, firmName, typeOfBusiness, businessRegistrationNumber,
                      legalStructure, phoneNumber, address, email, password) {
  return registrationRequest("firm", {
      registerDate,
      firmName,
      typeOfBusiness,
      businessRegistrationNumber,
      legalStructure,
      phoneNumber,
      address,
      email,
      password
  });
}

function getStudent(userId, token) {
  return accountInformationRetrievalRequest("student", userId, token);
}

function getFirm(userId, token) {
  return accountInformationRetrievalRequest("firm", userId, token);
}

function updateStudent(userId, updateData, token) {
  return accountInformationUpdateRequest("student", userId, updateData, token);
}

function updateFirm(userId, updateData, token) {
  return accountInformationUpdateRequest("firm", userId, updateData, token);
}

module.exports = {
  loginRequest,
  registerStudent,
  registerFirm,
  getStudent,
  getFirm,
  updateStudent,
  updateFirm
};
