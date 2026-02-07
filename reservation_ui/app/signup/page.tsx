'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

export default function SignUpPage() {
  const router = useRouter();
  const [form, setForm] = useState({
    loginId: '', password: '', email: '', nickname: '', phoneNumber: ''
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    // ✅ 수정: http://localhost:8080 제거하고 상대 경로인 /api/signup 사용
    const res = await fetch('/api/signup', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form),
    });

    if (res.ok) {
      alert('회원가입 성공! 로그인해주세요.');
      router.push('/login');
    } else {
      alert('회원가입 실패 (중복된 정보 등)');
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50">
      <form onSubmit={handleSubmit} className="w-full max-w-md space-y-4 bg-white p-8 shadow-lg rounded-2xl">
        <h2 className="text-2xl font-bold text-center">회원가입</h2>
        <input className="w-full p-3 border rounded" placeholder="아이디" onChange={e => setForm({...form, loginId: e.target.value})} required />
        <input className="w-full p-3 border rounded" type="password" placeholder="비밀번호" onChange={e => setForm({...form, password: e.target.value})} required />
        <input className="w-full p-3 border rounded" type="email" placeholder="이메일" onChange={e => setForm({...form, email: e.target.value})} required />
        <input className="w-full p-3 border rounded" placeholder="닉네임" onChange={e => setForm({...form, nickname: e.target.value})} />
        <input className="w-full p-3 border rounded" placeholder="전화번호" onChange={e => setForm({...form, phoneNumber: e.target.value})} />
        <button className="w-full bg-blue-600 text-white p-3 rounded font-bold hover:bg-blue-700">가입하기</button>
      </form>
    </div>
  );
}