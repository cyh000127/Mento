import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "@/api/axios";
import { useAuthStore } from "@/stores/useAuthStore";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";

export default function TestLoginPage() {
    const navigate = useNavigate();
    const { setAccessToken } = useAuthStore();

    const [email, setEmail] = useState("skincare01@example.com");
    const [password, setPassword] = useState("test1234");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            const response = await api.post("test/auth/login", {
                email,
                password,
            });

            const { accessToken } = response.data.data;

            setAccessToken(accessToken);
            navigate("/");

        } catch (err: any) {
            console.error(err);
            setError(err.response?.data?.message || "로그인에 실패했습니다.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <Card className="w-full max-w-md">
                <CardHeader>
                    <CardTitle className="text-2xl font-bold text-center">멘토 테스트 로그인</CardTitle>
                </CardHeader>
                <CardContent>
                    <form onSubmit={handleLogin} className="space-y-4">
                        <div className="space-y-2">
                            <Label htmlFor="email">이메일</Label> 
                            <Input
                                id="email"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="이메일 입력 (예: skincare01@example.com)"
                                required
                            />
                        </div>
                        <div className="space-y-2">
                            <Label htmlFor="password">비밀번호</Label>
                            <Input
                                id="password"
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                placeholder="비밀번호 입력"
                                required
                            />
                        </div>

                        {error && <div className="text-red-500 text-sm font-medium">{error}</div>}

                        <Button type="submit" className="w-full" disabled={loading}>
                            {loading ? "로그인 중..." : "로그인"}
                        </Button>

                        <div className="mt-4 p-4 bg-slate-50 rounded text-xs text-gray-500">
                            <p className="font-bold mb-1">테스트 계정 정보:</p>
                            <p>MENTOR: skincare01@example.com</p>
                            <p>MENTOR: beauty01@example.com</p>
                            <p>MENTOR: hair01@example.com</p>
                            <p>USER: user01@example.com</p>
                            <p>PW: test1234</p>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}