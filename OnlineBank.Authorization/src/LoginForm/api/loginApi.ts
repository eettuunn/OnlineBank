import axios from "axios";

export const apiBaseUrl = "http://127.0.0.1:3000/";

export type Credentials = {
  email: string;
  password: string;
};

export const login = (data: Credentials, url: string) => {
  console.log(url);
  axios
    .post(`${apiBaseUrl}user_api/user/login`, data)
    .then(
      ({ data }) => (location.href = `${url}?token=${data.token}&id=${data.id}`)
    )
    .catch((e) => console.log(e));
};
