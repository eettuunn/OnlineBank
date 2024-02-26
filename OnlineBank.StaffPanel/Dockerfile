FROM node:18-alpine AS builder

RUN apk add gettext

WORKDIR /src

COPY . .

RUN env \
 && npm clean-install\
 && npm run build

# ------------------------------------------------------------

FROM nginx:stable-alpine AS static

COPY ./static.nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=builder /src/build /usr/share/nginx/html/

