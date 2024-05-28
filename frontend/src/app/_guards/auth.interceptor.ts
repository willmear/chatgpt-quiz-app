import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Observable, catchError, throwError } from "rxjs";
import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { UserService } from "../service/user.servce";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    
    constructor(public userService: UserService, protected router: Router) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        if(req.headers.get('No-Auth') === 'True') {
            return next.handle(req.clone());
        }

        const token = this.userService.getToken();
        if (token) {
            req = this.addToken(req, token);
        }
        return next.handle(req);

    }

    private addToken(request: HttpRequest<any>, token:string) {
        return request.clone(
            {
                setHeaders: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                }
            }
        )
    }
    
}