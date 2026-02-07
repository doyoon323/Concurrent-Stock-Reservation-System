'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import PerformanceReport from './performanceReport';

export default function ReservationPage() {
  const router = useRouter();
  const [activeTab, setActiveTab] = useState('pessimistic'); 
  const [stock, setStock] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const fetchStock = async () => {
    try {
      const token = localStorage.getItem('accessToken');
      // ✅ 수정: http://localhost:8080 제거
      const response = await fetch('/api/products/12', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token ? `Bearer ${token}` : ''
        }
      });
      if (!response.ok) throw new Error('네트워크 응답 에러');
      const data = await response.json();
      if (data && data.stock) setStock(data.stock.quantity);
    } catch (error) {
      console.error("데이터 로드 실패:", error);
    }
  };

  useEffect(() => {
    fetchStock();
    const token = localStorage.getItem('accessToken');
    setIsLoggedIn(!!token);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('userId');
    setIsLoggedIn(false);
    alert('로그아웃 되었습니다.');
  };

  const handleReserve = async () => {
    const token = localStorage.getItem('accessToken');
    const userId = localStorage.getItem('userId');

    if (!token || !userId) {
      alert('로그인이 필요한 서비스입니다.');
      router.push('/login');
      return;
    }

    setLoading(true);
    setMessage('');

    try {
      // ✅ 수정: http://localhost:8080 제거
      const response = await fetch(`/api/reservations/${activeTab}`, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          userId: Number(userId),
          productId: 12,
          amount: 1
        }),
      });

      if (response.ok) {
        setMessage(`✅ [${activeTab.toUpperCase()}] 예약 성공!`);
        await fetchStock();
      } else {
        setMessage('❌ 예약 실패 (재고 부족 또는 서버 오류)');
      }
    } catch (error) {
      setMessage('⚠️ 서버 통신 에러');
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen bg-gray-50 flex flex-col items-center">
      
      {/* 1. 화면 최상단 고정 탭 바 */}
      <div className="fixed top-0 left-0 w-full flex justify-center p-4 bg-gray-50/80 backdrop-blur-md border-b border-gray-200 z-50">
        <div className="flex bg-gray-200/50 p-1 rounded-xl w-full max-w-md">
          {['pessimistic', 'optimistic', 'distributed'].map((tab) => (
            <button
              key={tab}
              onClick={() => { setActiveTab(tab); setMessage(''); }}
              className={`flex-1 py-2 text-xs font-bold rounded-lg transition-all ${
                activeTab === tab 
                  ? 'bg-black text-white shadow-md' 
                  : 'text-gray-400 hover:text-gray-600'
              }`}
            >
              {tab === 'pessimistic' ? '비관적 락' : tab === 'optimistic' ? '낙관적 락' : '분산 락'}
            </button>
          ))}
        </div>
      </div>

      {/* 상단 로그인/로그아웃 버튼 */}
      <div className="fixed top-20 right-6 z-40">
        {isLoggedIn ? (
          <button onClick={handleLogout} className="text-sm text-gray-500 hover:underline">로그아웃</button>
        ) : (
          <button onClick={() => router.push('/login')} className="text-sm text-blue-600 font-bold hover:underline">로그인</button>
        )}
      </div>

      {/* 2. 예약 섹션 */}
      <section className="h-[calc(100vh-120px)] w-full flex flex-col items-center justify-center relative">
        <div className="bg-white p-8 rounded-2xl shadow-xl w-full max-w-md text-center mt-12">
          <h1 className="text-3xl font-bold mb-6 text-gray-800">🍪 두쫀쿠 선착순 판매</h1>
          
          <div className="bg-blue-50 p-6 rounded-xl mb-6">
            <p className="text-gray-600 mb-2 font-medium">남은 재고</p>
            <p className="text-5xl font-black text-blue-600">
              {stock !== null ? `${stock}개` : '...'}
            </p>
          </div>

          <button
            onClick={handleReserve}
            disabled={loading || stock === 0}
            className={`w-full py-4 rounded-xl text-white font-bold text-lg transition-all ${
              loading || stock === 0 
                ? 'bg-gray-400 cursor-not-allowed' 
                : 'bg-black hover:bg-gray-800 active:scale-95'
            }`}
          >
            {loading ? '처리 중...' : stock === 0 ? '품절' : '지금 예약하기'}
          </button>

          {message && (
            <p className={`mt-4 font-medium ${message.includes('✅') ? 'text-green-600' : 'text-red-600'}`}>
              {message}
            </p>
          )}
        </div>
        
        <div className="absolute bottom-4 flex flex-col items-center animate-bounce">
          <p className="text-gray-400 text-[10px] mb-1 uppercase tracking-tighter">Performance Insight</p>
          <svg className="w-4 h-4 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7" />
          </svg>
        </div>
      </section>

      {/* 3. 분석 레포트 섹션 */}
      <section className="w-full max-w-4xl px-6 pb-20 pt-10 border-t border-gray-100 shadow-[0_-20px_40px_-15px_rgba(0,0,0,0.05)] bg-white rounded-t-[3rem]">
        <div className="text-center mb-12">
          <div className="inline-block px-4 py-1.5 bg-blue-600 text-white rounded-full text-[10px] font-black uppercase tracking-widest mb-4">
            Analysis Report
          </div>
          <h2 className="text-2xl font-bold text-gray-800">성능 및 구조</h2>
          <p className="mt-2 text-gray-400 text-sm">
            Backend Strategy: <span className="font-bold text-blue-600">{activeTab.toUpperCase()}</span>
          </p>
        </div>

        <PerformanceReport />
      </section>

    </main>
  );
}