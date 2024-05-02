const users = require('./users')

function testStudent() {
  const email = "studentname@university.edu";
  const password = "123456";

  const updatedEmail = 'studentname@gmail.com';
  const updatedPassword = 'abcdef';

  let loginToken = undefined;

  return users.registerStudent("123456789", "01.01.2000", "Abc", "Def", email, password)
    .then((userId) => {
      console.log(`Created user: ${userId}\n`);
      return users.loginRequest(email, password);
    })
    .then(([token, userId]) => {
      console.log(`Login token: ${token}\n`);
      loginToken = token;
      return users.getStudent(userId, token);
    })
    .then((student) => {
      console.log(`Student information: ${JSON.stringify(student)}\n`);
      return users.updateStudent(student['id'], {email: updatedEmail, password: updatedPassword}, loginToken);
    })
    .then((student) => {
      console.log(`Updated Student information: ${JSON.stringify(student)}\n`);
      return users.loginRequest(updatedEmail, updatedPassword);
    })
    .then(([token, userId]) => {
      console.log(`New Login Token: ${token}\n`);
      loginToken = token;
    })
    .catch((error) => {
      console.log(error)
    }
  );
}

function testFirm() {
  const email = "info@abcd.com";
  const password = "xyzt";

  const newAddress = "On Mars";
  const newPhone = "9876 543 21 09"

  let loginToken = undefined;

  return users.registerFirm(
               "01.01.2000",
               "ABCD",
               "Simple Model",
               "1234",
               "Structured",
               "0123 456 78 90",
               "On Earth",
               email, password)
    .then((userId) => {
      console.log(`Created user: ${userId}\n`);
      return users.loginRequest(email, password);
    })
    .then(([token, userId]) => {
      console.log(`Login token: ${token}\n`);
      loginToken = token;
      return users.getFirm(userId, token);
    })
    .then((firm) => {
      console.log(`Firm information: ${JSON.stringify(firm)}\n`);
      return users.updateFirm(firm['id'], {address: newAddress, phoneNumber: newPhone}, loginToken);
    })
    .then((firm) => {
      console.log(`Updated Firm information: ${JSON.stringify(firm)}\n`);
    })
    .catch((error) => {
      console.log(error)
    }
  );
}

testFirm().then(() => testStudent());

