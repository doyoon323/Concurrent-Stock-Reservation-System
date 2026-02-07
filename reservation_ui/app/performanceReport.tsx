// app/reservation/components/PerformanceReport.tsx
'use client';

export default function PerformanceReport() {
  return (
    <section className="w-full max-w-4xl mt-20 p-8 bg-white rounded-3xl shadow-sm border border-gray-100">
      <div className="border-b border-gray-100 pb-6 mb-8">
        <h2 className="text-2xl font-bold text-gray-800">📊 성능 분석 리포트</h2>
        <p className="text-gray-500 mt-2">비관적 락(Pessimistic Lock) 기반 동시성 제어 및 부하 테스트 결과</p>
      </div>

      {/* 0. 시스템 아키텍처 및 메커니즘 */}
      <div className="mb-12">
        <h3 className="text-lg font-bold mb-6 flex items-center">
          <span className="w-1 h-6 bg-black mr-3"></span>
          핵심 로직
        </h3>
        <div className="bg-gray-50 p-6 rounded-2xl border border-gray-100 flex flex-col items-center">
          <img
            src="lock.png" 
            alt="비관적 락 메커니즘 시퀀스 다이어그램"
            className="w-full max-w-3xl shadow-sm rounded-lg border border-gray-200 bg-white"
          />
          <div className="mt-4 text-sm text-gray-600 bg-white p-4 rounded-lg border border-gray-200 w-full max-w-3xl leading-relaxed">
            <p>
              💡 <b>설계 의도:</b> 정합성이 최우선인 선착순 시스템을 위해 <b>DB 레벨의 비관적 락(`FOR UPDATE`)</b>을 적용했습니다. 
              데이터 오차 0%를 보장하지만, 요청이 몰릴 경우 트랜잭션 대기열이 길어지는 특징이 있어 부하 테스트를 통해 최적의 임계점을 파악했습니다.
            </p>
          </div>
        </div>
      </div>

      {/* 1. 핵심 지표 요약 카드 (3단계 데이터를 임팩트 있게 요약) */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-12">
        <div className="p-6 bg-blue-50 rounded-2xl border border-blue-100">
          <p className="text-sm text-blue-600 font-bold mb-1">최고 처리량 (Peak)</p>
          <p className="text-3xl font-black text-blue-900 font-mono">289.46 <span className="text-lg font-sans">TPS</span></p>
          <p className="text-xs text-blue-400 mt-1">@ 500 VUs Scenario</p>
        </div>
        <div className="p-6 bg-red-50 rounded-2xl border border-orange-100">
          <p className="text-sm text-orange-600 font-bold mb-1">성능 임계점</p>
          <p className="text-3xl font-black text-orange-900">500 <span className="text-lg">VUs</span></p>
          <p className="text-xs text-red-400 mt-1">이후 성능 하락 발생</p>
        </div>
      </div>

      {/* 2. TPS 변화 (사용자 규모별 처리량) */}
      <div className="mb-12">
        <h3 className="text-lg font-bold mb-6 flex items-center">
          <span className="w-1 h-6 bg-black mr-3"></span>
          사용자 규모별 처리량 (TPS)
        </h3>
        <div className="space-y-6 bg-gray-50 p-6 rounded-2xl border border-gray-100">
          {/* Baseline */}
          <div>
            <div className="flex justify-between mb-2 text-sm font-medium text-gray-500">
              <span>100 VUs</span>
              <span>235.27 TPS</span>
            </div>
            <div className="w-full bg-gray-200 h-3 rounded-full overflow-hidden">
              <div className="bg-gray-400 h-full" style={{ width: '81%' }}></div>
            </div>
          </div>
          {/* Peak */}
          <div>
            <div className="flex justify-between mb-2 text-sm font-bold text-blue-600">
              <span>500 VUs</span>
              <span>289.46 TPS</span>
            </div>
            <div className="w-full bg-blue-200 h-5 rounded-full overflow-hidden shadow-inner">
              <div className="bg-blue-600 h-full" style={{ width: '100%' }}></div>
            </div>
          </div>
          {/* Stress */}
          <div>
            <div className="flex justify-between mb-2 text-sm font-medium text-red-500">
              <span>1000 VUs</span>
              <span>260.96 TPS</span>
            </div>
            <div className="w-full bg-gray-200 h-3 rounded-full overflow-hidden">
              <div className="bg-red-400 h-full" style={{ width: '90%' }}></div>
            </div>
            <p className="mt-2 text-[11px] text-red-400 italic font-medium">
              * 1000 VUs 구간: 락 경합 및 컨텍스트 스위칭 오버헤드로 인해 처리량 10% 감소 확인
            </p>
          </div>
        </div>
      </div>

      {/* 3. 상세 성능 지표 비교 (Table) */}
      <div className="mb-12">
        <h3 className="text-lg font-bold mb-6 flex items-center">
          <span className="w-1 h-6 bg-black mr-3"></span>
          상세 메트릭 비교
        </h3>
        <div className="overflow-hidden border border-gray-100 rounded-2xl shadow-sm">
          <table className="w-full text-left text-sm">
            <thead className="bg-gray-800 text-white font-medium">
              <tr>
                <th className="px-6 py-4">구분</th>
                <th className="px-6 py-4 text-center">100 VUs</th>
                <th className="px-6 py-4 text-center bg-gray-700">500 VUs</th>
                <th className="px-6 py-4 text-center">1000 VUs</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              <tr>
                <td className="px-6 py-4 font-bold text-gray-700 bg-gray-50/50">p(95) Latency</td>
                <td className="px-6 py-4 text-center font-mono">477.73ms</td>
                <td className="px-6 py-4 text-center font-mono font-bold text-blue-600 bg-blue-50/30">1.03s</td>
                <td className="px-6 py-4 text-center font-mono text-red-500">4.75s (All)</td>
              </tr>
              <tr>
                <td className="px-6 py-4 font-bold text-gray-700 bg-gray-50/50">성공률 (정합성)</td>
                <td className="px-6 py-4 text-center">100%</td>
                <td className="px-6 py-4 text-center font-bold text-blue-600 bg-blue-50/30">50% (Sold out)</td>
                <td className="px-6 py-4 text-center">25% (Sold out)</td>
              </tr>
              <tr>
                <td className="px-6 py-4 font-bold text-gray-700 bg-gray-50/50">Avg duration</td>
                <td className="px-6 py-4 text-center font-mono">305ms</td>
                <td className="px-6 py-4 text-center font-mono bg-blue-50/30">570ms</td>
                <td className="px-4 py-4 text-center font-mono">2.65s</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      {/* 4. 최적화 기록 (HikariCP) */}
      <div className="mb-8 p-6 bg-white border border-gray-100 rounded-2xl shadow-sm">
        <p className="text-sm text-gray-500  leading-relaxed">
          부하 테스트를 통해 커넥션 대기 시간과 DB 락 경합을 최소화하는 <b>최적 커넥션 풀 사이즈(18)</b>를 도출했습니다.
        </p>
    </div>

      {/* 노션 링크 버튼 */}
      <div className="flex justify-center mt-12 border-t border-gray-100 pt-10">
        <a 
          href="https://daffodil-sponge-820.notion.site/2d817c3b003480b58366e9fbcebc1c8a#2e117c3b0034804690d1da1fc3f1cf02" 
          target="_blank"
          className="px-8 py-4 bg-gray-900 text-white rounded-full text-sm font-bold hover:bg-black hover:scale-105 transition-all shadow-lg flex items-center gap-2"
        >
          <span>📖</span> 전체 실험 로그 및 트러블슈팅 더 보기
        </a>
      </div>
    </section>
  );
}