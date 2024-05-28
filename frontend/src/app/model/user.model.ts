export interface IUser {
    id: number;
    firstname: string;
    lastname: string;
    email: string;
    school: String;
    password: string;
    role: string;
    confirmationPassword?: string;
}

export interface User {
    id: number;
    firstname: string;
    lastname: string;
}

export type NewUser = Omit<IUser, 'id'> & { id: null };
export type AuthUser = Omit<IUser, 'id' | 'firstname' | 'lastname' | 'school' | 'role' | 'confirmationPassword'>